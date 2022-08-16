package com.teledoc.globalid.graphql.configuration;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphqlConfiguration {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return builder -> builder.scalar(ExtendedScalars.UUID)
                .scalar(ExtendedScalars.DateTime).scalar(ExtendedScalars.Date);
    }

}
