package com.teledoc.globalid.graphql.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ConsumerInput {
    private NameInput[] names;
    private ContactPointInput[] contactPoints;
    private AdministrativeGender gender;
    private GenderIdentity genderIdentity;
    private LocalDate birthDate;
    private AddressInput[] addresses;
}
