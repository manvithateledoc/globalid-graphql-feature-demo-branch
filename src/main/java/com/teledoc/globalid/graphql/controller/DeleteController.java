package com.teledoc.globalid.graphql.controller;

import com.teledoc.globalid.graphql.entities.Output;
import com.teledoc.globalid.graphql.entities.identities.GlobalID;
import com.teledoc.globalid.graphql.service.DeleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DeleteController {

    private final DeleteService deleteService;

    @MutationMapping
    public Output dropIdentity(@Argument GlobalID globalIdInput){
        return deleteService.dropById(globalIdInput);

    }

}
