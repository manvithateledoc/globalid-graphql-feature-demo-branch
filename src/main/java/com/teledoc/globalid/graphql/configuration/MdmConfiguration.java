package com.teledoc.globalid.graphql.configuration;

import madison.mpi.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.rmi.ServerException;

@Configuration
public class MdmConfiguration {

    @Value("${host:localhost}")
    private String host;

    @Value("${port:9081}")
    private int port;

    @Value("${userId:mdmadmin}")
    private String userId;

    @Value("${password:mdmadmin}")
    private String password;

    @Bean
    public Context context() {
        var ctx = new Context(host, port, userId, password);
        if (!ctx.isConnected()) {
            throw new IllegalStateException("MDM context not connected: " + ctx.getErrMsg());
        }
        return ctx;
    }
}
