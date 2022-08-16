package com.teledoc.globalid.graphql;

import com.teledoc.globalid.graphql.entities.identities.LocalID;
import com.teledoc.globalid.graphql.service.RetrieveIdentityService;
import madison.mpi.Context;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.rmi.ServerException;

class RetrieveIdentityServiceTest {

    private final RetrieveIdentityService service = new RetrieveIdentityService(Mockito.mock(Context.class));

    @Test
    void retrieveIdentity() throws ServerException {
        var globalIDS = service.retrieveIdentity ( new LocalID() );
        Assertions.assertNotNull ( globalIDS );
    }
}
