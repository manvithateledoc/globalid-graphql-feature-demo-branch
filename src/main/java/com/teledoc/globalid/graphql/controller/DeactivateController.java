package com.teledoc.globalid.graphql.controller;


import com.teledoc.globalid.graphql.entities.Output;
import com.teledoc.globalid.graphql.entities.identities.GlobalID;
import com.teledoc.globalid.graphql.service.DeactivateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.rmi.ServerException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DeactivateController {

    private final DeactivateService deactivateService;
    public Output deleteById(@Argument  GlobalID identity){
        return deactivateService.deleteById(identity);


    }
}

