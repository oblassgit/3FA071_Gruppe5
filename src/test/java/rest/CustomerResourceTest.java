package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.Reading;
import dev.hv.rest.resource.ReadingList;
import jakarta.ws.rs.core.Response;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import dev.hv.model.Customer;
import dev.hv.rest.resource.CustomerList;
import dev.hv.rest.resource.CustomerResource;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private static UUID testUuid1;
    private static UUID testUuid2;
    private static Customer mockCustomer1;
    private static Customer mockCustomer2;

    @BeforeAll
    public static void before() {
        mockDbConnection = mock(DatabaseCon.class);
        mockCustomerDao = mock(CustomerDao.class);

        testUuid1 = UUID.randomUUID();
        testUuid2 = UUID.randomUUID();
        mockCustomer1 = new Customer(testUuid1, "John", "Doe", LocalDate.now(), Gender.M);
        mockCustomer2 = new Customer(testUuid2, "John", "Doe", LocalDate.now(), Gender.M);
        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));

        customerResource = new CustomerResource(mockDbConnection, mockCustomerDao);
    }

    @Test
    public void testGetCustomerByUuid() throws SQLException, JsonProcessingException {
        when(mockCustomerDao.getCustomer(testUuid1)).thenReturn(mockCustomer1);


        Response response = customerResource.getCustomer(testUuid1.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockCustomer1, response.getEntity());

        JSONObject schemaJson = new JSONObject(new JSONTokener(
                Objects.requireNonNull(getClass().getResourceAsStream("/json schemas/JSON_Schema_Customer.json"))));
        Schema schema = SchemaLoader.load(schemaJson);

        JSONObject responseJson = new JSONObject(jsonResponse);
        assertDoesNotThrow(() -> schema.validate(responseJson), "This JSON does not conform to the provided schema.");
    }

    @Test
    public void testGetCustomerByUuidNotFound() throws SQLException, JsonProcessingException {
        UUID testUuid = UUID.randomUUID();

        Mockito.doThrow(new SQLException()).when(mockCustomerDao).getCustomer(testUuid);

        Response response = customerResource.getCustomer(testUuid.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(null, response.getEntity());
    }

    @Test
    public void testUpdateCustomerOk() throws SQLException {
        Mockito.doNothing().when(mockCustomerDao).updateCustomer(mockCustomer1);

        Response response = customerResource.updateCustomer(mockCustomer1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Customer with uuid: " + testUuid1 + " was updated.", response.getEntity());
    } 

    @Test
    public void testUpdateCustomerNotFound() throws SQLException, JsonProcessingException {
        Mockito.doThrow(new SQLException()).when(mockCustomerDao).updateCustomer(mockCustomer1);

        Response response = customerResource.updateCustomer(mockCustomer1);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Could not find customer with uuid: " + testUuid1, response.getEntity());
    } 
    
    @Test
    public void testGetAllCustomers() throws SQLException, JsonProcessingException {
        List<Customer> allCustomers = new ArrayList<>();
        allCustomers.add(mockCustomer1);
        allCustomers.add(mockCustomer2);
        CustomerList customerList = new CustomerList(allCustomers);

        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));
        when(mockCustomerDao.getAllCustomers()).thenReturn(allCustomers);

        Response response = customerResource.getAllCustomers();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(customerList, response.getEntity());

        JSONObject schemaJson = new JSONObject(new JSONTokener(
                Objects.requireNonNull(getClass().getResourceAsStream("/json schemas/JSON_Schema_Customers.json"))));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        Schema schema = SchemaLoader.load(schemaJson);

        JSONObject responseJson = new JSONObject(jsonResponse);
        assertDoesNotThrow(() -> schema.validate(responseJson), "This JSON does not conform to the provided schema.");
    }

    @Test
    public void testCreateCustomer() throws SQLException, JsonProcessingException {
        Mockito.doNothing().when(mockCustomerDao).createCustomer(mockCustomer1);

        Response response = customerResource.createCustomer(mockCustomer1);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        JSONObject schemaJson = new JSONObject(new JSONTokener(
                Objects.requireNonNull(getClass().getResourceAsStream("/json schemas/JSON_Schema_Customer.json"))));
        Schema schema = SchemaLoader.load(schemaJson);

        JSONObject responseJson = new JSONObject(jsonResponse);
        assertDoesNotThrow(() -> schema.validate(responseJson), "This JSON does not conform to the provided schema.");
    }

    @Test
    public void testDeleteCustomerOK() throws SQLException, JsonProcessingException {
        Reading mockReading = new Reading(UUID.randomUUID(), "", mockCustomer1, LocalDate.now(), KindOfMeter.HEIZUNG,
                0.0, "", false);
        Reading mockReading1 = new Reading(UUID.randomUUID(), "", mockCustomer1, LocalDate.now(), KindOfMeter.HEIZUNG,
                0.0, "", false);

        List<Reading> readings = new ArrayList<>();
        readings.add(mockReading);
        readings.add(mockReading1);
        ReadingList readingList = new ReadingList(readings);

        Mockito.doNothing().when(mockCustomerDao).deleteCustomer(mockCustomer1);
        when(mockCustomerDao.getCustomer(testUuid1)).thenReturn(mockCustomer1);
        when(mockCustomerDao.getReadingsForCustomer(mockCustomer1)).thenReturn(readingList);

        Response response = customerResource.deleteCustomer(testUuid1.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        JSONObject schemaJson = new JSONObject(new JSONTokener(Objects.requireNonNull(
                getClass().getResourceAsStream("/json schemas/JSON_Schema_CustomerWithReadings.json"))));
        Schema schema = SchemaLoader.load(schemaJson);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        JSONObject responseJson = new JSONObject(jsonResponse);
        assertDoesNotThrow(() -> schema.validate(responseJson), "This JSON does not conform to the provided schema.");
    }

    @Test
    public void testDeleteCustomerNotFound() throws SQLException, JsonProcessingException {
        Mockito.doNothing().when(mockCustomerDao).deleteCustomer(mockCustomer1);
        when(mockCustomerDao.getCustomer(testUuid1)).thenReturn(null);
        when(mockCustomerDao.getReadingsForCustomer(mockCustomer1)).thenReturn(null);

        Response response = customerResource.deleteCustomer(testUuid1.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

}
