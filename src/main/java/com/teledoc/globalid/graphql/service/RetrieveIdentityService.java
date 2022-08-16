package com.teledoc.globalid.graphql.service;

import com.teledoc.globalid.graphql.MDMInputConverter;
import com.teledoc.globalid.graphql.MDMOutputConverter;
import com.teledoc.globalid.graphql.entities.identities.GlobalID;
import com.teledoc.globalid.graphql.entities.identities.LocalID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madison.mpi.*;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RetrieveIdentityService {

    private MDMInputConverter inputConverter = new MDMInputConverter();

    private MDMOutputConverter outputConverter = new MDMOutputConverter();

    private final Context context;

    public List<GlobalID> retrieveIdentity(GlobalID identity) {


        var memGet = new IxnMemGet(context);

        //MemRowList searchRows = this.getInputConverter().convert(consumerInput);
        var searchRows =new MemRowList();

        var memHead = new MemHead();
        memHead.setSrcCode(identity.getRoot());
        memHead.setMemRecno(Long.valueOf( identity.getId()));
        searchRows.addRow(memHead);

        // Set a segment code filter to limit
        // output to specific segments.
        memGet.setSegCodeFilter("MEMHEAD,MEMATTR,MEMNAME,MEMADDR,MEMPHONE,MEMIDENT,MEMDATE");

        // Set the record status indicators desired.
        // The values include (A)ctive, (I)nactive, (D)eleted and (S)hadow.


        // Set the member type as PERSON.
        // Member types are listed in mpi_memtype table.
        memGet.setMemType("PERSON");


        var outMemRows = new MemRowList();

        boolean result = memGet.execute(searchRows, outMemRows,GetType.UNKNOWN, KeyType.UNKNOWN);
        memGet.getErrText();
        log.debug("Found results: " + result);
        log.info(memGet.getErrText());
        //Iterating the outMemRows

        var rowIter = outMemRows.rows();
        var results = new ArrayList<GlobalID>();

        while (rowIter.hasMoreRows()) {
            var memRow = (MemRow) rowIter.nextRow();
            var id = new GlobalID();
            id.setId("" + memRow.getEntRecno());
            var issuedOn = OffsetDateTime.ofInstant(memRow.getMemHead().getRecMtime().toInstant(), ZoneOffset.UTC);
            id.setIssuedOn(issuedOn);
            id.setRoot(memRow.getSrcCode());
            memRow.getEntRecno();
            results.add(id);
        }

        return results;
    }
}
