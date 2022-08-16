package com.teledoc.globalid.graphql.service;

import com.teledoc.globalid.graphql.MDMInputConverter;
import com.teledoc.globalid.graphql.MDMOutputConverter;
import com.teledoc.globalid.graphql.entities.ConsumerInput;
import com.teledoc.globalid.graphql.entities.identities.Identity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madison.mpi.*;
import madison.util.SetterException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchIdentityService {

    private MDMInputConverter inputConverter = new MDMInputConverter();

    private MDMOutputConverter outputConverter = new MDMOutputConverter();

    private final Context context;

    public List<Identity> searchIdentity(ConsumerInput consumerInput) throws SetterException {
        var memSearch = new IxnMemSearch(context);
        log.info("Are we connected: " + context.isConnected());

        var searchRows = inputConverter.convert(consumerInput);

        // Set the member type as PERSON.
        // Member types are listed in mpi_memtype table.
        memSearch.setMemType("Person");
        memSearch.setEntType("patient");


        // Set a segment code filter to limit
        // output to specific segments.
        memSearch.setSegCodeFilter("MEMHEAD,MEMATTR,MEMNAME,MEMADDR,MEMPHONE,MEMIDENT,MEMDATE");

        // Set the record status indicators desired.
        // The values include (A)ctive, (I)nactive, (D)eleted and (S)hadow.
        memSearch.setRecStatFilter("A,I");

        // Set memstat value as A - Active, O- Overlay.
        memSearch.setMemStatFilter("A,O");
        memSearch.setMinScore((short) 0);
        memSearch.setMaxRows(50);

        var outMemRows = new MemRowList();

        boolean result = memSearch.execute(searchRows, outMemRows, GetType.ASENTITY, SearchType.ASMEMBER);

        log.info("Found results: " + result);
        log.info(memSearch.getErrText());
        outputConverter.convert(outMemRows);


        return outputConverter.convert(outMemRows);
    }

}
