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

    @BeforeAll
    public static void before() {
        mockDbConnection = mock(DatabaseCon.class);
        mockCustomerDao = mock(CustomerDao.class);
        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));

        customerResource = new CustomerResource(mockDbConnection, mockCustomerDao);
    }

    @Test
    public void testGetCustomerByUuid() throws SQLException, JsonProcessingException {

        // Arrange
        UUID testUuid = UUID.randomUUID();
        Customer mockCustomer = new Customer(testUuid, "John", "Doe", LocalDate.now(), Gender.M);

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

    @Test
    public void testUpdateCustomer() throws SQLException, JsonProcessingException {
        UUID testUuid = UUID.randomUUID();
        Customer mockCustomer = new Customer(testUuid, "John", "Doe", LocalDate.now(), Gender.M);

        Mockito.doNothing().when(mockCustomerDao).updateCustomer(mockCustomer);

        Response response = customerResource.updateCustomer(mockCustomer);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Customer with uuid: " + testUuid + " was updated.", response.getEntity());
    } 

    @Test
    public void testCreateCustomer() throws SQLException, JsonProcessingException {
        UUID testUuid = UUID.randomUUID();
        Customer mockCustomer = new Customer(testUuid, "John", "Doe", LocalDate.now(), Gender.M);

        Mockito.doNothing().when(mockCustomerDao).createCustomer(mockCustomer);

        Response response = customerResource.createCustomer(mockCustomer);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        JSONObject schemaJson = new JSONObject(new JSONTokener(Objects.requireNonNull(getClass().getResourceAsStream("/json schemas/JSON_Schema_Customer.json"))));
        Schema schema = SchemaLoader.load(schemaJson);

        JSONObject responseJson = new JSONObject(jsonResponse);
        assertDoesNotThrow(() -> schema.validate(responseJson), "This JSON does not conform to the provided schema.");
    }

    @Test
    public void testDeleteCustomer() throws SQLException, JsonProcessingException {
        UUID testUuid = UUID.randomUUID();
        Customer mockCustomer = new Customer(testUuid, "John", "Doe", LocalDate.now(), Gender.M);
        Reading mockReading = new Reading(UUID.randomUUID(), "", mockCustomer, LocalDate.now(), KindOfMeter.HEIZUNG, 0.0, "", false);
        Reading mockReading1 = new Reading(UUID.randomUUID(), "", mockCustomer, LocalDate.now(), KindOfMeter.HEIZUNG, 0.0, "", false);


        List<Reading> readings = new ArrayList<>();
        readings.add(mockReading);
        readings.add(mockReading1);
        ReadingList readingList = new ReadingList(readings);

        Mockito.doNothing().when(mockCustomerDao).deleteCustomer(mockCustomer);
        when(mockCustomerDao.getCustomer(testUuid)).thenReturn(mockCustomer);
        when(mockCustomerDao.getReadingsForCustomer(mockCustomer)).thenReturn(readingList);


        Response response = customerResource.deleteCustomer(testUuid.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        JSONObject schemaJson = new JSONObject(new JSONTokener(Objects.requireNonNull(getClass().getResourceAsStream("/json schemas/JSON_Schema_CustomerWithReadings.json"))));
        Schema schema = SchemaLoader.load(schemaJson);

        JSONObject responseJson = new JSONObject(jsonResponse);
        assertDoesNotThrow(() -> schema.validate(responseJson), "This JSON does not conform to the provided schema.");
    }

}
