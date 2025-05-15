package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.CustomerWrapper;
import dev.hv.model.Reading;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomerResourceTest {

    private static CustomerResource customerResource;
    private static DatabaseCon mockDbConnection;
    private static CustomerDao mockCustomerDao;
    private static UUID testUuid1;
    private static UUID testUuid2;
    private static Customer mockCustomerNoId;
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
        mockCustomerNoId = new Customer(null, "John", "Doe", LocalDate.now(), Gender.M);
        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));

        customerResource = new CustomerResource(mockDbConnection, mockCustomerDao);
    }

    @Test
    public void testGetCustomerByUuid() throws SQLException, JsonProcessingException {
        // Mocking the DAO call
        when(mockCustomerDao.getCustomer(testUuid1)).thenReturn(mockCustomer1);

        // Calling the API endpoint
        Response response = customerResource.getCustomer(testUuid1.toString());

        // Deserialize response entity to CustomerResponse
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerWrapper customerWrapper = objectMapper.convertValue(response.getEntity(), CustomerWrapper.class);

        // Debugging: Print formatted JSON output
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(customerWrapper);
        System.out.println(jsonResponse);

        // Assertions
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockCustomer1, customerWrapper.getCustomer()); // Compare actual customer objects

        // Load and validate against JSON schema
        JSONObject schemaJson = new JSONObject(new JSONTokener(
                Objects.requireNonNull(getClass().getResourceAsStream("/json schemas/JSON_Schema_Customer.json"))));
        Schema schema = SchemaLoader.load(schemaJson);

        // Convert JSON string to JSONObject for validation
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
        assertNull(response.getEntity());
    }

    @Test
    public void testUpdateCustomerOk() throws SQLException {
        Mockito.doNothing().when(mockCustomerDao).updateCustomer(mockCustomer1);

        Response response = customerResource.updateCustomer(new CustomerWrapper(mockCustomer1));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Customer with uuid: " + testUuid1 + " was updated.", response.getEntity());
    } 

    @Test
    public void testUpdateCustomerNotFound() throws SQLException, JsonProcessingException {
        Mockito.doThrow(new SQLException()).when(mockCustomerDao).updateCustomer(mockCustomer1);

        Response response = customerResource.updateCustomer(new CustomerWrapper(mockCustomer1));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Could not find customer with uuid: " + testUuid1, response.getEntity());
    }

    @Test

    public void testGetCustomers() throws SQLException, JsonProcessingException {
        List<Customer> allCustomers = new ArrayList<>();
        allCustomers.add(mockCustomer1);
        allCustomers.add(mockCustomer2);

        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));
        when(mockCustomerDao.getCustomers(null, null, null)).thenReturn(allCustomers);

        Response response = customerResource.getCustomers(null, null, null);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Deserialize response entity
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerList actualResponse = objectMapper.convertValue(response.getEntity(), CustomerList.class);

        // Debugging: Print JSON response
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualResponse);
        System.out.println(jsonResponse);


        assertEquals(2, actualResponse.getCustomers().size());

        // Validate against JSON schema
        JSONObject schemaJson = new JSONObject(new JSONTokener(
                Objects.requireNonNull(getClass().getResourceAsStream("/json schemas/JSON_Schema_Customers.json"))));
        Schema schema = SchemaLoader.load(schemaJson);

        JSONObject responseJson = new JSONObject(jsonResponse);
        assertDoesNotThrow(() -> schema.validate(responseJson), "This JSON does not conform to the provided schema.");
    }


    @Test
    public void testGetCustomersWithGenderFilter() throws SQLException {
        List<Customer> maleCustomers = new ArrayList<>();
        maleCustomers.add(mockCustomer1);

        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));
        when(mockCustomerDao.getCustomers(null, null, Gender.M)).thenReturn(maleCustomers);

        Response response = customerResource.getCustomers(null, null, "M");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetCustomersWithDateRange() throws SQLException {
        List<Customer> dateFilteredCustomers = new ArrayList<>();
        dateFilteredCustomers.add(mockCustomer1);

        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();

        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));
        when(mockCustomerDao.getCustomers(startDate, endDate, null)).thenReturn(dateFilteredCustomers);

        Response response = customerResource.getCustomers(startDate.toString(), endDate.toString(), null);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetCustomersBadRequest() {
        String invalidStartDate = "invalid-date";

        Response response = customerResource.getCustomers(invalidStartDate, null, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Invalid start format", response.getEntity());
    }

    @Test
    public void testCreateCustomerCreated() throws SQLException, JsonProcessingException {
        Mockito.doNothing().when(mockCustomerDao).createCustomer(mockCustomer1);

        Response response = customerResource.createCustomer(new CustomerWrapper(mockCustomer1));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        JSONObject schemaJson = new JSONObject(new JSONTokener(
                Objects.requireNonNull(getClass().getResourceAsStream("/json schemas/JSON_Schema_Customer.json"))));
        Schema schema = SchemaLoader.load(schemaJson);

        JSONObject responseJson = new JSONObject(jsonResponse);
        assertDoesNotThrow(() -> schema.validate(responseJson), "This JSON does not conform to the provided schema.");
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCustomerWithoutIdCreated() throws SQLException, JsonProcessingException {
        Mockito.doNothing().when(mockCustomerDao).createCustomer(mockCustomerNoId);

        Response response = customerResource.createCustomer(new CustomerWrapper(mockCustomerNoId));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        JSONObject schemaJson = new JSONObject(new JSONTokener(
                Objects.requireNonNull(getClass().getResourceAsStream("/json schemas/JSON_Schema_Customer.json"))));
        Schema schema = SchemaLoader.load(schemaJson);

        JSONObject responseJson = new JSONObject(jsonResponse);
        assertDoesNotThrow(() -> schema.validate(responseJson), "This JSON does not conform to the provided schema.");
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateCustomerBadRequest() throws SQLException, JsonProcessingException {
        Mockito.doThrow(new SQLException()).when(mockCustomerDao).createCustomer(mockCustomer1);

        Response response = customerResource.createCustomer(new CustomerWrapper(mockCustomer1));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
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

        Mockito.doNothing().when(mockCustomerDao).deleteCustomer(mockCustomer1);
        when(mockCustomerDao.getCustomer(testUuid1)).thenReturn(mockCustomer1);
        when(mockCustomerDao.getReadingsForCustomer(mockCustomer1)).thenReturn(readings);

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
