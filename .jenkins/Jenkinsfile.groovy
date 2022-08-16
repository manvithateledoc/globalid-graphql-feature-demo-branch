@Library('cloudbees-devops-global-pipeline@master') _
@Library('pipeline-shared@cloudbees-test') tdoc

pipeline {
  agent {
    kubernetes {
        label "tdh-eod-primary-pipeline-service-template-nodes"
        yamlFile ".jenkins/jenkins-pod.yaml"
    }
  }

  environment {
    cmkArn = credentials('devops-svc-kms-key')
    CLUSTER_DOMAIN="${params.PLATFORM}.${params.DOMAIN.trim()}"
    DOMAIN="${params.PLATFORM.equalsIgnoreCase('tdock') ? params.DOMAIN.trim() : env.CLUSTER_DOMAIN}"
    REPO="${GIT_URL.tokenize('/.')[-2]}"
    COMMIT="${GIT_COMMIT.substring(0, Math.min(GIT_COMMIT.length(), 10))}"
    LOWERCASE_BRANCH="${GIT_BRANCH.toLowerCase().replaceAll('^[0-9]', '').replaceAll('[^a-z0-9]', '-').replaceAll('-+', '-').replaceAll('(^-+|-+$)', '').take(63)}"
    NAMESPACE="${(params.NAMESPACE?.trim()) ?: env.LOWERCASE_BRANCH}"
    AWS_CREDENTIALS_ID="aws-credentials-devops"
    MEDULLA_SUBDIR="medulla@${env.BUILD_NUMBER}"
    VERSION = "3.4" // must be greater-than-or-equal-to upstream job

    IMAGE_NAME="${REPO.replaceAll('_', '-')}"
    BUILD_TAG="commit-${COMMIT}"
    DEPLOY_TAG="deploy-${params.PLATFORM}-${params.DATACENTER}-${params.CLUSTER}-${NAMESPACE}-${params.CLASS}-${COMMIT}"
    ARTIFACTORY_PREFIX="globalid"
    DOCKER_REGISTRY_CREDENTIALS_ID="artifactory-svc-docker-promoter"
    DOCKER_REGISTRY_DOMAIN = "artifactory.intouchhealth.io"
    DOCKER_REGISTRY_TYPE = "docker-eod-local"
    DOCKER_REGISTRY = "docker.${env.DOCKER_REGISTRY_DOMAIN}/${env.ARTIFACTORY_PREFIX}" // the virtual docker image registry used for pulling images
    DOCKER_REGISTRY_PUSH = "${env.DOCKER_REGISTRY_TYPE}.${env.DOCKER_REGISTRY_DOMAIN}/${env.ARTIFACTORY_PREFIX}" // the address is used to push docker images to the registry
    DOCKER_REGISTRY_API_URL = "${env.DOCKER_REGISTRY_DOMAIN}/artifactory/api/docker/${env.DOCKER_REGISTRY_TYPE}/v2/${env.ARTIFACTORY_PREFIX}" // the address used for operations requiring API access, such as retagging
  }

  options {
    buildDiscarder(logRotator(daysToKeepStr: '30'))
    timeout(time: 3, unit: 'HOURS')
    skipStagesAfterUnstable()
  }

  parameters {
    string(defaultValue: 'globalid', description: 'Namespace Target (e.g. lowercase branch)', name: 'NAMESPACE')
    choice(choices: "aws\nonprem", description: 'Data Center', name: 'DATACENTER')
    string(defaultValue: "globalid", description: 'Cluster name (e.g. dev, qa, uat)', name: 'CLUSTER')
    string(defaultValue: 'tdock', description: 'Platform Name', name: 'PLATFORM')
    string(defaultValue: 'teladoc.io', description: 'Domain', name: 'DOMAIN')
    string(defaultValue: 'USA', description: 'Country for Application Stack (e.g USA, CAN, EU)', name: 'LOCATION')
    choice(choices: "us-east-1\nus-east-2", description: 'AWS Region', name: 'AWS_REGION')
    choice(choices: "ephemeral\npersistent", description: 'Environment type', name: 'CLASS')
    choice(choices: "private\npublic", description: 'Environment visibility.\nprivate - environment will be accessible only from whitelisted locations.\npublic - environment will be accessible from', name: 'VISIBILITY')
    string(defaultValue: '3.4', description: 'Enables DevOps to require a force update of your branch', name: 'VERSION')
  }

  stages {
    stage('Start') {
      steps {
        container('aws-cli') {
          script {
            env.DOCKER_IMAGE_EXISTS=dockerImageExists(DOCKER_REGISTRY_API_URL, DOCKER_REGISTRY_CREDENTIALS_ID, IMAGE_NAME, BUILD_TAG)
          }
        }
      }
    }

    stage('Decrypt Maven Settings File') {
      steps {
        container("aws-encryption-cli") {
          script {
            devopsKms.mavenencrypt()
            devopsKms.mavendecrypt()
          }
        }
      }
    }

    stage('Build') {
      when {
        expression { DOCKER_IMAGE_EXISTS == 'false' }
      }
      steps {
        container("docker") {
          script {
            withEnv(["DOCKER_REGISTRY=${env.DOCKER_REGISTRY_TYPE}.${env.DOCKER_REGISTRY_DOMAIN}/${env.ARTIFACTORY_PREFIX}"]) {
              withCredentials([usernamePassword(credentialsId: DOCKER_REGISTRY_CREDENTIALS_ID, usernameVariable: 'DOCKER_REGISTRY_CREDS_USR', passwordVariable: 'DOCKER_REGISTRY_CREDS_PSW')]) {
                sh "docker login -u ${DOCKER_REGISTRY_CREDS_USR} -p ${DOCKER_REGISTRY_CREDS_PSW} https://docker.${env.DOCKER_REGISTRY_DOMAIN}/${env.ARTIFACTORY_PREFIX}"
              }
              unstash 'settings-xml'
              sh 'chown 1000:1000 settings.xml'
              sh "docker build --no-cache --network=host -t ${DOCKER_REGISTRY_PUSH}/${IMAGE_NAME}:${BUILD_TAG} -f Dockerfile ."
            }
          }
        }
      }
    }

    stage('Push') {
      when {
        expression { DOCKER_IMAGE_EXISTS == 'false' }
      }
      steps {
        container("docker") {
          script {
            // Lines below are to remove annoying Docker config defaults
            sh 'rm ~/.dockercfg || true'
            sh 'rm ~/.docker/config.json || true'
            // End Docker config removal
            docker.withRegistry("https://${DOCKER_REGISTRY_PUSH}", DOCKER_REGISTRY_CREDENTIALS_ID) {
              // image.push()
              sh "docker push ${DOCKER_REGISTRY_PUSH}/${IMAGE_NAME}:${BUILD_TAG}"
            }
          }
        }
      }
    }

    stage('Tag') {
      steps {
        container("aws-cli") {
          script {
            if (GIT_BRANCH.equalsIgnoreCase('master')) {
              dockerTagImage(DOCKER_REGISTRY_API_URL, DOCKER_REGISTRY_CREDENTIALS_ID, IMAGE_NAME, BUILD_TAG, "latest")
            }
            if (params.CLUSTER.trim() != '') {
              dockerTagImage(DOCKER_REGISTRY_API_URL, DOCKER_REGISTRY_CREDENTIALS_ID, IMAGE_NAME, BUILD_TAG, DEPLOY_TAG)
            }
          }
        }
      }
    }

    stage('Deploy') {
      when { expression { params.DATACENTER?.trim() && params.CLUSTER?.trim() } }
      environment {
        CLUSTER = "${params.CLUSTER}"
        LOCATION = "${params.LOCATION}"
        KUBECONFIG = "/home/jenkins/workspace/${env.JOB_NAME}@${env.BUILD_NUMBER}/.kube/config"
      }
      stages {
        stage('configure kubectl') {
          steps {
            container('eod-tools') {
              script {
                cloneMedulla(MEDULLA_SUBDIR)
                configureKubectl(MEDULLA_SUBDIR)
              }
            }
          }
        }

        // only deploy if namespace exists
        stage('kubectl apply') {
          when {
            expression {
              container('eod-tools') {
                status = sh(returnStatus: true, script: 'cd $MEDULLA_SUBDIR && kubectl get namespace $NAMESPACE --no-headers --output=go-template={{.metadata.name}}')
                if(status != 0 && status != 1) {
                  error("Error while checking if namespace exists")
                }
                // 0/false if namespace found, 1/true if missing (or other error)
                return !status
              }
            }
          }
          steps {
            container('eod-tools') {
              withCredentials([[
                $class: 'AmazonWebServicesCredentialsBinding',
                credentialsId: AWS_CREDENTIALS_ID,
                accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
              ]]) {
                dir(MEDULLA_SUBDIR) {
                  script {
                    sh "./create_manifest.sh > ../manifest.yml"
                    sh "cd ../ && kubectl apply -f manifest.yml"
                  }
                }
              }
            }
          }
          post {
            success {
              archiveArtifacts artifacts: "manifest.yml"
            }
            failure {
              archiveArtifacts artifacts: "manifest.yml"
            }
            aborted {
              archiveArtifacts artifacts: "manifest.yml"
            }
          }
        }
      }
    }
  }

  post {
    cleanup {
      container("aws-cli") {
        sh "chmod -R 777 ."
        deleteDir()
        dir("${workspace}@tmp") {
          deleteDir()
        }
      }
    }
  }
}
