package com.teledoc.globalid.graphql;

import com.teledoc.globalid.graphql.entities.*;
import com.teledoc.globalid.graphql.entities.identities.GlobalID;
import com.teledoc.globalid.graphql.service.RetrieveIdentityService;
import com.teledoc.globalid.graphql.service.UpsertService;
import madison.mpi.Context;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
@Ignore
class UpsertServiceTest {
    ConsumerInput consumerInput;
    UpsertService upsertService;
    @Mock
    Context context;

    @Mock
    RetrieveIdentityService retrieveIdentityService;

    @BeforeEach
    public void setup() {
        upsertService = new UpsertService(context, retrieveIdentityService);

        consumerInput = new ConsumerInput ();
        NameInput nameInput = new NameInput ();
        nameInput.setFamily ( "abc" );
        nameInput.setGiven ( new String[]{"def", "ijk"} );
        nameInput.setPrefix ( "ex" );
        nameInput.setSuffix ( "pos" );
        NameInput[] nameInputs = new NameInput[]{nameInput};
        AddressInput addressInput = new AddressInput ();
        addressInput.setCity ( "nyc" );
        addressInput.setCountry ( "US" );
        addressInput.setUse ( AddressUse.OLD );
        addressInput.setPostalCode ( "123456" );
        addressInput.setLine ( Arrays.asList ( new String[]{"ind", "US"} ) );
        ContactPointInput contactPointInput = new ContactPointInput ();
        contactPointInput.setRank ( 1 );
        contactPointInput.setValue ( "add" );
        contactPointInput.setSystem ( ContactPointSystem.PHONE );
        contactPointInput.setUse ( ContactPointUse.OLD );
        consumerInput.setNames ( nameInputs );
        consumerInput.setAddresses ( new AddressInput[]{addressInput} );
        consumerInput.setContactPoints ( new ContactPointInput[]{contactPointInput} );
    }

    @Test
    void registerNewEntity() throws Exception {
        GlobalID globalID = upsertService.registerNewEntity ( consumerInput, null, null );

        Assertions.assertNotNull ( globalID );
    }




}
