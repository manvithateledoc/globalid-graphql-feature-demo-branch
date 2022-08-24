package com.teledoc.globalid.graphql;

import com.teledoc.globalid.graphql.entities.*;
import madison.mpi.MemAddr;
import madison.mpi.MemRow;
import madison.mpi.MemRowList;
import madison.util.SetterException;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
@Ignore
public class MDMInputConverterTest {

    ConsumerInput consumerInput;
    private MemRowList memberRows;

    @BeforeEach
    public void setup() {
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
    public void inputConverter() throws SetterException {
        MDMInputConverter mdmInputConverter = new MDMInputConverter ();
        MemRowList memRowList = mdmInputConverter.convert ( consumerInput );
        MemRow row = memRowList.rowAt( 1 );
        Assertions.assertNotNull ( memRowList );
        Assertions.assertEquals ( 6, memRowList.size () );
        Assertions.assertEquals( "nyc",((MemAddr) row).getCity() );
        Assertions.assertEquals( "US" ,((MemAddr) row).getCountry() );
        Assertions.assertEquals( "123456" ,((MemAddr) row).getZipCode() );
    }
}
