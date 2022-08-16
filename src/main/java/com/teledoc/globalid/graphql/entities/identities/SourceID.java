package com.teledoc.globalid.graphql.entities.identities;

import lombok.Data;

import java.util.Date;
@Data
public class SourceID extends Identity{
    protected String srcCode;
}
