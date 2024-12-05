package rest;


import jakarta.ws.rs.core.Response;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import dev.hv.model.Customer;
import dev.hv.rest.resource.CustomerResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

import static jdk.jfr.internal.jfc.model.Constraint.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CustomerResourceTest {

    private static CustomerResource customerResource; // Deine Jersey-Ressource
    private static DatabaseCon mockDbConnection;
    private static CustomerDao mockCustomerDao;

    @BeforeAll
    public static void before() {
        mockDbConnection = mock(DatabaseCon.class);
        mockCustomerDao = mock(CustomerDao.class);
        customerResource = new CustomerResource();
    }

    @Test
    public void testGetCustomerByUuid() throws SQLException {

        // Arrange
        UUID testUuid = UUID.randomUUID();
        Customer mockCustomer = new Customer(testUuid, "John", "Doe", LocalDate.now(), Gender.M);

        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));
        when(mockCustomerDao.getCustomer(testUuid)).thenReturn(mockCustomer);


        // Act
        Response response = customerResource.getCustomer(testUuid.toString());

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockCustomer, response.getEntity());
    }

}
