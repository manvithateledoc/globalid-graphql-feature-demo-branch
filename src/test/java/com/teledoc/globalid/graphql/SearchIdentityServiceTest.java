/*
package com.teledoc.globalid.graphql;

import com.teledoc.globalid.graphql.entities.*;
import com.teledoc.globalid.graphql.entities.identities.Identity;
import madison.mpi.Context;
import madison.util.SetterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

class SearchIdentityServiceTest {

    private final SearchIdentityService service = new SearchIdentityService(Mockito.mock(Context.class));

    private ConsumerInput consumerInput;
    {
        consumerInput = new ConsumerInput();
        var nameInput = new NameInput ();
        nameInput.setFamily ( "abc" );
        nameInput.setGiven ( new String[]{"def", "ijk"} );
        nameInput.setPrefix ( "ex" );
        nameInput.setSuffix ( "pos" );
        var nameInputs = new NameInput[]{nameInput};
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
    void searchIdentity() throws SetterException {

        List<Identity> identities = service.searchIdentity ( consumerInput );

        Assertions.assertNotNull ( identities );
    }
}
*/
