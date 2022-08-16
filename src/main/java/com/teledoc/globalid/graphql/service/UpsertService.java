package com.teledoc.globalid.graphql.service;


import com.teledoc.globalid.graphql.MDMInputConverter;
import com.teledoc.globalid.graphql.MDMOutputConverter;
import com.teledoc.globalid.graphql.entities.ConsumerInput;
import com.teledoc.globalid.graphql.entities.identities.GlobalID;
import com.teledoc.globalid.graphql.entities.identities.LocalID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madison.mpi.*;
import madison.util.SetterException;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpsertService {

    private MDMInputConverter inputConverter = new MDMInputConverter();

    private MDMOutputConverter outputConverter = new MDMOutputConverter();

    private final Context context;

    private final RetrieveIdentityService retrieveIdentityService;

    protected MDMInputConverter getInputConverter(){
        return this.inputConverter;
    }

    protected MDMOutputConverter getOutputConverter(){
        return this.outputConverter;
    }

    public GlobalID registerNewEntity(ConsumerInput consumerInput, String source, String id) throws ServerException, SetterException {
        var ixnMemPut = new IxnMemPut(context);

        // Create member rowlists to hold input and output row(s).
        var      inpMemRows = new MemRowList();
        var      outMemRows = new MemRowList();
        var memHead    = new MemHead();
        // Set identifier data - srcCode/memIdnum
        memHead.setSrcCode(source);
        memHead.setMemIdnum(id);

       // Set attributes for the member.
       MemAttr memAttr = new MemAttr();
       memAttr.setMemRecno(memHead.getMemRecno());
        memAttr.setMemSeqno(memHead.generateNextMemSeqno());


         // Add this member information to the member row list **/
        inpMemRows.addRow(memHead);
        inpMemRows = this.getInputConverter().convert(inpMemRows, consumerInput);

        boolean registerNewEntity = ixnMemPut.execute(inpMemRows, outMemRows, PutType.INSERT_UPDATE, MemMode.ATTRCOMP, MatchMode.DONOTHING );
        log.info("is create successful : " +registerNewEntity);
        log.info(ixnMemPut.getErrText());

        var globalID = new GlobalID();
        //Iterating the outMemRows
        var rowIter = outMemRows.rows();
        //Iterating the outMemRows

        var results = new ArrayList<GlobalID>();

        while (rowIter.hasMoreRows()) {
            MemRow memRow = (MemRow) rowIter.nextRow();
            globalID.setId("" + memRow.getEntRecno());
            var issuedOn = OffsetDateTime.ofInstant(memRow.getMemHead().getRecMtime().toInstant(), ZoneOffset.UTC);

            globalID.setIssuedOn(issuedOn);
            globalID.setRoot("US");
            memRow.getEntRecno();
            results.add(globalID);
        }

        LocalID identity = new LocalID();
        identity.setId(id);
        identity.setRoot(source);

        List<GlobalID> globalIDS  = retrieveIdentityService.retrieveIdentity( identity);
        if(globalIDS.size() > 0){
            globalID.setId(globalIDS.get(0).getId());
        }
        return  globalID;

    }

}
