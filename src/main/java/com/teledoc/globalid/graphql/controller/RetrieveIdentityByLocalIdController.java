package com.teledoc.globalid.graphql.controller;

import com.teledoc.globalid.graphql.entities.identities.LocalID;
import com.teledoc.globalid.graphql.entities.identities.SourceID;
import com.teledoc.globalid.graphql.service.RetrieveIdentityByLocalIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RetrieveIdentityByLocalIdController {

    private final RetrieveIdentityByLocalIdService retrieveIdentityByLocalIdService;



    @MutationMapping
    public List<SourceID> retrieveIdentityByLocalId(@Argument LocalID identity) {
        return retrieveIdentityByLocalIdService.retrieveIdentityByLocalId(identity);
    }
}
