package com.teledoc.globalid.graphql.entities;

import lombok.Data;

@Data
public class ContactPointInput {
    private ContactPointSystem system;
    private String value;
    private ContactPointUse use;
    private int rank;
    private PeriodInput period;
}
