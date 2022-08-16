package com.teledoc.globalid.graphql.entities.identities;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Identity {

    protected String id;

    protected String root;

    protected OffsetDateTime issuedOn;
}