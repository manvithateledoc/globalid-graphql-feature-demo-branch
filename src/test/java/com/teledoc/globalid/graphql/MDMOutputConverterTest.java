package com.teledoc.globalid.graphql;

import com.teledoc.globalid.graphql.entities.identities.Identity;
import madison.mpi.*;
import madison.util.SetterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MDMOutputConverterTest {
    MDMOutputConverter mdmOutputConverter;
    MemRowList memRowList;

    @BeforeEach
    public void setup() {
        mdmOutputConverter = new MDMOutputConverter();
        memRowList = new MemRowList();

        MemHead memHead = new MemHead();
        // Set identifier data - srcCode/memIdnum
        memHead.setSrcCode( "RMC" );
        memHead.setMemIdnum( "1002423" );

        // Set attributes for the member.
        MemAttr memAttr = new MemAttr();
        memAttr.setMemRecno( memHead.getMemRecno() );
        memAttr.setEntRecno( memHead.getEntRecno() );
        memAttr.setMemSeqno( memHead.generateNextMemSeqno() );

        // Set the segment attribute code(Listed in mpi_segattr table)
        memAttr.setAttrCode( "RACE" );
        memAttr.setAttrVal( "01" );
        memAttr.setRowInd( RowInd.INSERT );

        MemName memName = new MemName( memHead );

        // Set the segment attribute code
        memName.setAttrCode( "LGLNAME" );
        memName.setOnmFirst( "Rick" );
        memName.setOnmLast( "Jones" );
        memName.setRowInd( RowInd.INSERT );

        MemRow maIds = new MemRow( "MemAttr" );

        try {
            //init row with MemRecno/EntRecno of memHead
            maIds.init( memHead );
            maIds.setString( "AttrCode", "SEX" );
            maIds.setString( "AttrVal", "M" );
            maIds.setEnumRowInd( "RowInd", RowInd.INSERT );
        } catch (SetterException e) {
        }

        // Add this member information to the member row list
        memRowList.addRow( maIds );
    }

    @Test
    public void testMdmOutputConverter() {
        List<Identity> identities = mdmOutputConverter.convert( memRowList );
        Assertions.assertNotNull( identities );
        Assertions.assertEquals( 1, identities.size() );
        Assertions.assertEquals( "1002423", identities.get( 0 ).getId() );


    }
}
