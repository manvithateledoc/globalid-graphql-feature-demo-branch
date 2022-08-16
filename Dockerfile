#
# Build stage
#
FROM maven:3.8.6-openjdk-11-slim as builder

# Create Working Directory
ENV BUILD_DIR="/home/build"
RUN mkdir $BUILD_DIR
WORKDIR $BUILD_DIR

# This comes from Jenkins job
COPY settings.xml .

# Copy Source Code
COPY pom.xml .
COPY src src

# Build Jar
RUN mvn -s settings.xml package -Dmaven.test.skip

# Rename jar
RUN export PROJECT_NAME=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.artifactId -q -DforceStdout) && \
  export PROJECT_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout) && \
  cd target/ && \
  mv ./${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

#
# Deploy stage
#
FROM openjdk:11-jre-slim-buster

# Create app directory
ENV APP_HOME=/home/app
RUN mkdir -p $APP_HOME
WORKDIR $APP_HOME

# Copy jar file over from builder stage
COPY --from=builder /home/build/target/app.jar $APP_HOME

EXPOSE 8080

CMD exec java $JAVA_OPTS -jar ./app.jar