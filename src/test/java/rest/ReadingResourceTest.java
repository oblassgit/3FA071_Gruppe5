package rest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.db.ReadingDao;
import dev.hv.enums.Gender;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.Customer;
import dev.hv.model.Reading;
import dev.hv.rest.resource.CustomerResource;
import dev.hv.rest.resource.ReadingResource;
import jakarta.ws.rs.core.Response;

public class ReadingResourceTest {

    private static CustomerResource customerResource;
    private static DatabaseCon mockDbConnection;
    private static CustomerDao mockCustomerDao;
    private static ReadingDao mockreadingDao;
    private static Customer mockCustomer;
    private static Reading mockReading;
    private static 

    @BeforeAll
    public static void before() {
        UUID testUuid = UUID.randomUUID();

        mockDbConnection = mock(DatabaseCon.class);
        mockCustomerDao = mock(CustomerDao.class);
        mockCustomer = mock(Customer.class);
        mockreadingDao = mock(ReadingDao.class);
        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));

        customerResource = new CustomerResource(mockDbConnection, mockCustomerDao);
        Customer mockCustomer = new Customer(testUuid, "John", "Doe", LocalDate.now(), Gender.M);
    }

    @Test
    public void testCreateReading() throws SQLException {
        UUID uuid = UUID.randomUUID();
        Reading reading = new Reading(uuid, "test", mockCustomer, LocalDate.now(), KindOfMeter.HEIZUNG, 2.0, "1", true);
        Mockito.doNothing().when(mockreadingDao).createReading(reading);

        Response response = customerResource.(uuid.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getEntity());
        System.out.println(jsonResponse);

        JSONObject schemaJson = new JSONObject(new JSONTokener(Objects.requireNonNull(getClass().getResourceAsStream("/json schemas/JSON_Schema_CustomerWithReadings.json"))));
        Schema schema = SchemaLoader.load(schemaJson);

        JSONObject responseJson = new JSONObject(jsonResponse);
        assertDoesNotThrow(() -> schema.validate(responseJson), "This JSON does not conform to the provided schema.");
    }
    }
