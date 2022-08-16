package com.teledoc.globalid.graphql.controller;

import com.teledoc.globalid.graphql.entities.identities.GlobalID;
import com.teledoc.globalid.graphql.service.RetrieveIdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.rmi.ServerException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RetrieveIdentityController {

    private final RetrieveIdentityService retrieveIdentityService;

    @QueryMapping
    public List<GlobalID> retrieveIdentity(GlobalID identity) throws ServerException {
        return retrieveIdentityService.retrieveIdentity(identity);
    }
}
