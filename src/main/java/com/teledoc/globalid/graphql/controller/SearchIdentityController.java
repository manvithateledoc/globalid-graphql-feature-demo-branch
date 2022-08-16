package com.teledoc.globalid.graphql.controller;

import com.teledoc.globalid.graphql.entities.ConsumerInput;
import com.teledoc.globalid.graphql.entities.identities.Identity;
import com.teledoc.globalid.graphql.service.SearchIdentityService;
import lombok.RequiredArgsConstructor;
import madison.util.SetterException;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchIdentityController {

    private final SearchIdentityService searchIdentityService;

    @QueryMapping
    public List<Identity> searchIdentity(ConsumerInput consumerInput) throws SetterException {
        return searchIdentityService.searchIdentity(consumerInput);
    }
}
