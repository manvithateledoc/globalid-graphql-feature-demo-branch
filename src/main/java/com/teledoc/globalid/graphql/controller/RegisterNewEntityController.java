package com.teledoc.globalid.graphql.controller;

import com.teledoc.globalid.graphql.entities.ConsumerInput;
import com.teledoc.globalid.graphql.entities.identities.GlobalID;
import com.teledoc.globalid.graphql.service.UpsertService;
import lombok.AllArgsConstructor;
import madison.util.SetterException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.rmi.ServerException;

@Controller
@AllArgsConstructor
public class RegisterNewEntityController {


    private final UpsertService upsertService;

    @MutationMapping
    public GlobalID registerNewEntity(@Argument @NotNull ConsumerInput person, @NotEmpty @Argument String root, @NotEmpty @Argument String id) throws SetterException, ServerException {
        return upsertService.registerNewEntity(person, root, id);
    }
}
