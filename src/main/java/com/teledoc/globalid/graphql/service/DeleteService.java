package com.teledoc.globalid.graphql.service;


import com.teledoc.globalid.graphql.entities.Output;
import com.teledoc.globalid.graphql.entities.identities.GlobalID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madison.mpi.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteService {

    private final Context context;
    private final RetrieveIdentityService retrieveIdentityService;

    private final Producer producer;
    private final String DELETE_TOPIC="test-del-gid-topic";

    public Output dropById(GlobalID globalID) {
        IxnMemDrop ixnMemDrop = new IxnMemDrop(context);
        producer.sendDeleteMessage( globalID,DELETE_TOPIC );

        MemRowList searchRows =new MemRowList();
        ixnMemDrop.setMemType("PERSON");
        var outMemRows = new MemRowList();
        var sourceIDS = retrieveIdentityService.retrieveIdentity( globalID );
        MemHead memHead = new MemHead();
        memHead.setSrcCode(sourceIDS.get( 0 ).getRoot());
        memHead.setMemIdnum(  sourceIDS.get( 0 ).getId()  );
        searchRows.addRow(memHead);

        boolean result = ixnMemDrop.execute(searchRows, KeyType.MEMIDNUM);
        log.info("Success fully deleted  "+result);
        Output output = new Output();
        output.setDeleted( result );
        return output;
    }

}
