package rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;

import org.junit.jupiter.api.BeforeAll;

import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.rest.resource.CustomerResource;

public class ReadingResourceTest {
    
 private static CustomerResource customerResource;
    private static DatabaseCon mockDbConnection;
    private static CustomerDao mockCustomerDao;

    @BeforeAll
    public static void before() {
        mockDbConnection = mock(DatabaseCon.class);
        mockCustomerDao = mock(CustomerDao.class);
        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));

        customerResource = new CustomerResource(mockDbConnection, mockCustomerDao);
    }

    
    
}
