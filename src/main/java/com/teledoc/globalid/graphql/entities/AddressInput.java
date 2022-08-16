package com.teledoc.globalid.graphql.entities;

import lombok.Data;

import java.util.List;

@Data
public class AddressInput {
    private AddressUse use;
    private List<String> line;
    private String city;
    private String state;
    private  String postalCode;
    private String district;
    private String country;
//    private PeriodInput period;
}
