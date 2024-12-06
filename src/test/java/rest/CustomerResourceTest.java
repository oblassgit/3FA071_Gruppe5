package rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import dev.hv.model.Customer;
import dev.hv.rest.resource.CustomerResource;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CustomerResourceTest {

    private static CustomerResource customerResource;
    private static DatabaseCon mockDbConnection;
    private static CustomerDao mockCustomerDao;

    @BeforeAll
    public static void before() {
        mockDbConnection = mock(DatabaseCon.class);
        mockCustomerDao = mock(CustomerDao.class);
        customerResource = new CustomerResource(mockDbConnection, mockCustomerDao);
    }

    @Test
    public void testGetCustomerByUuid() throws SQLException, JsonProcessingException {

        // Arrange
        UUID testUuid = UUID.randomUUID();
        Customer mockCustomer = new Customer(testUuid, "John", "Doe", LocalDate.now(), Gender.M);

        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));
        when(mockCustomerDao.getCustomer(testUuid)).thenReturn(mockCustomer);


        Response response = customerResource.getCustomer(testUuid.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockCustomer, response.getEntity());

        JSONObject schemaJson = new JSONObject(new JSONTokener(Objects.requireNonNull(getClass().getResourceAsStream("/json schemas/JSON_Schema_Customer.json"))));
        Schema schema = SchemaLoader.load(schemaJson);

        JSONObject responseJson = new JSONObject(jsonResponse);
        assertDoesNotThrow(() -> schema.validate(responseJson), "This JSON does not conform to the provided schema.");
    }

}
