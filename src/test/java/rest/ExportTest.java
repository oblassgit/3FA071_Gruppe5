package rest;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExportTest {
    private CustomerResource customerResource;
    private ReadingResource readingResource;
    private DatabaseCon mockDbConnection;
    private CustomerDao mockCustomerDao;
    private ReadingDao mockReadingDao;
    private UUID customer1ID;
    private UUID customer2ID;
    private Customer mockCustomer1;
    private Customer mockCustomer2;
    private UUID reading1ID;
    private UUID reading2ID;
    private Reading mockReading1;
    private Reading mockReading2;
    private ArrayList<Customer> customerList;
    private ArrayList<Reading> readingList;

    @BeforeEach
    public void before() {
        mockDbConnection = mock(DatabaseCon.class);
        mockCustomerDao = mock(CustomerDao.class);
        mockReadingDao = mock(ReadingDao.class);

        when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));

        // Customer
        customer1ID = UUID.randomUUID();
        customer2ID = UUID.randomUUID();
        mockCustomer1 = new Customer(customer1ID, "John", "Doe", LocalDate.now(), Gender.M);
        mockCustomer2 = new Customer(customer2ID, "Hans", "Wurst", LocalDate.now(), Gender.M);

        customerList = new ArrayList<>();
        customerList.add(mockCustomer1);
        customerList.add(mockCustomer2);

        customerResource = new CustomerResource(mockDbConnection, mockCustomerDao);

        //Reading
        reading1ID = UUID.randomUUID();
        reading2ID = UUID.randomUUID();
        mockReading1 = new Reading(reading1ID, "blablabla", mockCustomer1, LocalDate.now(), KindOfMeter.HEIZUNG, 0.0, "", false);
        mockReading2 = new Reading(reading2ID, "blablablablub", mockCustomer2, LocalDate.now(), KindOfMeter.WASSER, 0.0, "", false);

        readingList = new ArrayList<>();
        readingList.add(mockReading1);
        readingList.add(mockReading2);

        readingResource = new ReadingResource(mockDbConnection, mockReadingDao, mockCustomerDao);
    }

    // CSV TESTS
    @Test
    public void testCSVCustomerExportSuccess() throws SQLException {
        when(mockCustomerDao.getAllCustomers()).thenReturn(customerList);

        Response response = customerResource.exportCSV();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("text/csv", response.getMediaType().toString());

        assertTrue(response.getEntity().toString().contains("birthDate,firstName,gender,lastName,uuid"));
        assertTrue(response.getEntity().toString().contains("John"));
    }

    @Test
    public void testCSVCustomerExportFailure() throws SQLException {
        // Simulate an exception when retrieving customers
        when(mockCustomerDao.getAllCustomers()).thenThrow(new RuntimeException("Database error"));

        Response response = customerResource.exportCSV();

        assertNotNull(response, "Response should not be null");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus(),
                "Should return 500 Internal Server Error");

        // Ensure the error message is present
        assertNotNull(response.getEntity(), "Error message should be present in response");
        assertTrue(response.getEntity().toString().contains("Error exporting CSV"),
                "Response should contain an appropriate error message");
    }

    @Test
    public void testCSVReadingExportSuccess() throws SQLException {
        when(mockReadingDao.getReadings(null,null,null,null)).thenReturn(readingList);

        Response response = readingResource.exportCSV();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("text/csv", response.getMediaType().toString());

        assertTrue(response.getEntity().toString().contains("id,comment,customerId,dateOfReading,kindOfMeter,meterCount,meterId,substitute"));
        assertTrue(response.getEntity().toString().contains("blablabla"));
    }

    @Test
    public void testCSVReadingExportFailure() throws SQLException {
        // Simulate an exception when retrieving customers
        when(mockReadingDao.getReadings(null,null,null,null)).thenThrow(new RuntimeException("Database error"));

        Response response = readingResource.exportCSV();

        assertNotNull(response, "Response should not be null");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus(),
                "Should return 500 Internal Server Error");

        // Ensure the error message is present
        assertNotNull(response.getEntity(), "Error message should be present in response");
        assertTrue(response.getEntity().toString().contains("Error exporting CSV"),
                "Response should contain an appropriate error message");
    }

    // XML TESTS

    @Test
    void testXMLReadingExportSuccess() throws SQLException {
        when(mockReadingDao.getReadings(null, null, null, null)).thenReturn(readingList);

        Response response = readingResource.exportXML();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("application/xml", response.getMediaType().toString());
        assertTrue(response.getEntity().toString().contains("<kindOfMeter>"));
        assertTrue(response.getEntity().toString().contains("<comment>blablabla</comment>"));
    }

    @Test
    void testXMLCustomerExportSuccess() throws SQLException {
        when(mockCustomerDao.getAllCustomers()).thenReturn(customerList);

        Response response = customerResource.exportXML();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("application/xml", response.getMediaType().toString());
        assertTrue(response.getEntity().toString().contains("<gender>"));
        assertTrue(response.getEntity().toString().contains("<firstName>John</firstName>"));
    }

    @Test
    public void testXMLCustomerExportFailure() throws SQLException {
        // Simulate an exception when retrieving customers
        when(mockCustomerDao.getAllCustomers()).thenThrow(new RuntimeException("Database error"));

        Response response = customerResource.exportXML();

        assertNotNull(response, "Response should not be null");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus(),
                "Should return 500 Internal Server Error");

        // Ensure the error message is present
        assertNotNull(response.getEntity(), "Error message should be present in response");
        assertTrue(response.getEntity().toString().contains("Error exporting XML"),
                "Response should contain an appropriate error message");
    }

    @Test
    public void testXMLReadingExportFailure() throws SQLException {
        // Simulate an exception when retrieving readings
        when(mockReadingDao.getReadings(null, null, null, null)).thenThrow(new RuntimeException("Database error"));

        Response response = readingResource.exportXML();

        assertNotNull(response, "Response should not be null");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus(),
                "Should return 500 Internal Server Error");

        // Ensure the error message is present
        assertNotNull(response.getEntity(), "Error message should be present in response");
        assertTrue(response.getEntity().toString().contains("Error exporting XML"),
                "Response should contain an appropriate error message");
    }

}
