package com.teledoc.globalid.graphql;

import com.teledoc.globalid.graphql.entities.identities.Identity;
import com.teledoc.globalid.graphql.entities.identities.LocalID;
import madison.mpi.MemRow;
import madison.mpi.MemRowList;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class MDMOutputConverter {

    public List<Identity> convert(MemRowList memRows){

        //Iterating the outMemRows
        var rowIter = memRows.rows();

        List<Identity> results = new ArrayList<Identity>();

        while (rowIter.hasMoreRows()) {
            var memRow = (MemRow) rowIter.nextRow();

            var memId = memRow.getMemHead().getMemIdnum();

            var id = new LocalID();
            id.setId(memId);

            var issuedOn = OffsetDateTime.ofInstant(memRow.getMemHead().getRecCtime().toInstant(), ZoneOffset.UTC);

            id.setIssuedOn(issuedOn);

            results.add(id);

        }

        return results;
    }

}
