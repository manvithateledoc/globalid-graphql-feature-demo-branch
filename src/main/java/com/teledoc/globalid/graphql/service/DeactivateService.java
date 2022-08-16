package com.teledoc.globalid.graphql.service;


import com.teledoc.globalid.graphql.entities.Output;
import com.teledoc.globalid.graphql.entities.identities.GlobalID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madison.mpi.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeactivateService {

    private final Context context;
    private final RetrieveIdentityService retrieveIdentityService;

    public Output deleteById(GlobalID globalID) {
        var ixnMemDelete = new IxnMemDelete(context);

        //MemRowList searchRows = this.getInputConverter().convert(consumerInput);
        var searchRows =new MemRowList();
        ixnMemDelete.setMemType("PERSON");
        var sourceIDS = retrieveIdentityService.retrieveIdentity( globalID );
        var memHead = new MemHead();
        memHead.setSrcCode(sourceIDS.get( 0 ).getRoot());
        memHead.setMemIdnum(  sourceIDS.get( 0 ).getId()  );
        searchRows.addRow(memHead);

        var result = ixnMemDelete.execute(searchRows, KeyType.MEMIDNUM);
        log.info("Success fully deleted  "+result);
        Output output = new Output();
        output.setDeleted( result );
        return output;
    }


}
