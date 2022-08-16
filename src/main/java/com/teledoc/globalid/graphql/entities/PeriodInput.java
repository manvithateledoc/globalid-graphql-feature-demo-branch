package com.teledoc.globalid.graphql.entities;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class PeriodInput {
    private OffsetDateTime start;
    private OffsetDateTime end;
}
