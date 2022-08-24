package com.teledoc.globalid.graphql;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@Ignore
public class GraphQLApplicationTest {
    @Test
    public void contextLoads() {
    }

    @Test
    public void applicationStarts() {
        GraphQlApplication.main ( new String[]{} );
    }
}
