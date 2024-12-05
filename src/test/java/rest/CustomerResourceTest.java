package rest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.hv.Util;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.rest.resource.CustomerResource;
import org.mockito.Mockito;

public class CustomerResourceTest {
    CustomerResource customerResource;

    @Mock
    private DatabaseCon databaseCon;      // Mocking the DatabaseCon class
    @Mock
    private Util util;                     // Mocking the Util class
    @Mock
    private CustomerDao customerDao;       // Mocking the CustomerDao class

    @BeforeEach
    public void before() throws IOException {
        customerResource = new CustomerResource();
        MockitoAnnotations.openMocks(this); 
        
        
    }

    @Test
    public void getAllCustomersTest() {
        customerResource.getAllCustomers();

    }


    
}
