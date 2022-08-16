package com.teledoc.globalid.graphql.entities;

import lombok.Data;

@Data
public class NameInput {
    private NameUse use;
    private String family;
    private String[] given;
    private String prefix;
    private String suffix;
    private PeriodInput period;
}
