package rest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.hv.db.DatabaseCon;
import dev.hv.db.ReadingDao;
import dev.hv.enums.Gender;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.Customer;
import dev.hv.model.Reading;
import dev.hv.rest.resource.ReadingResource;
import jakarta.ws.rs.core.Response;

public class ReadingResourceTest {
        private static ReadingResource readingResource;
        private static ReadingDao mockReadingDao;
        private static DatabaseCon mockDbConnection;
        private static Customer mockCustomer;

        @BeforeAll
        public static void before() throws SQLException {
                mockDbConnection = mock(DatabaseCon.class);
                when(mockDbConnection.getConnection()).thenReturn(mock(Connection.class));

                mockCustomer = new Customer(UUID.randomUUID(), "Hugh", "Jass", LocalDate.now(), Gender.D);

                mockReadingDao = mock(ReadingDao.class);
                readingResource = new ReadingResource(mockDbConnection, mockReadingDao);
        }

        @Test
        public void testDeleteReadingOk() throws SQLException, JsonProcessingException {
                UUID mockReadingUuid = UUID.randomUUID();
                Reading mockReading = new Reading(mockReadingUuid, "This is a comment", mockCustomer, LocalDate.now(),
                                KindOfMeter.WASSER, 2.5, "12345", false);

                Mockito.doNothing().when(mockReadingDao).deleteReading(mockReading);
                when(mockReadingDao.getReading(mockReadingUuid)).thenReturn(mockReading);

                Response response = readingResource.deleteReading(mockReadingUuid.toString());
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(response.getEntity());
                System.out.println(jsonResponse);

                JSONObject schemaJson = new JSONObject(new JSONTokener(Objects.requireNonNull(
                                getClass().getResourceAsStream("/json schemas/JSON_Schema_Reading.json"))));
                Schema schema = SchemaLoader.load(schemaJson);

                JSONObject responseJson = new JSONObject(jsonResponse);
                assertDoesNotThrow(() -> schema.validate(responseJson),
                                "This JSON does not conform to the provided schema.");
        }

        @Test
        public void testDeleteReadingNotFound() throws SQLException, JsonProcessingException {
                UUID mockReadingUuid = UUID.randomUUID();
                Reading mockReading = new Reading(mockReadingUuid, "This is a comment", mockCustomer, LocalDate.now(),
                                KindOfMeter.WASSER, 2.5, "12345", false);

                Mockito.doNothing().when(mockReadingDao).deleteReading(mockReading);

                Response response = readingResource.deleteReading(mockReadingUuid.toString());

                assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
                assertEquals("Could not find reading with uuid : " + mockReadingUuid, response.getEntity());
        }

        @Test
        public void testCreateReading() throws SQLException, JsonProcessingException {
                UUID uuid = UUID.randomUUID();
                Reading reading = new Reading(uuid, "test", mockCustomer, LocalDate.now(), KindOfMeter.HEIZUNG, 2.0,
                                "1", true);
                Mockito.doNothing().when(mockReadingDao).createReading(reading);

                Response response = readingResource.createReading(reading);
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(response.getEntity());
                System.out.println(jsonResponse);

                JSONObject schemaJson = new JSONObject(new JSONTokener(
                                Objects.requireNonNull(getClass()
                                                .getResourceAsStream("/json schemas/JSON_Schema_Reading.json"))));
                Schema schema = SchemaLoader.load(schemaJson);

                assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

                JSONObject responseJson = new JSONObject(jsonResponse);
                assertDoesNotThrow(() -> schema.validate(responseJson),
                                "This JSON does not conform to the provided schema.");
        }

        @Test
        public void testGetReading() throws SQLException, JsonProcessingException {
                UUID uuid = UUID.randomUUID();
                Reading reading = new Reading(uuid, "test", mockCustomer, LocalDate.now(), KindOfMeter.HEIZUNG, 2.0,
                                "1", true);
                Mockito.doReturn(reading).when(mockReadingDao).getReading(uuid);

                Response response = readingResource.getReading(uuid.toString());
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(response.getEntity());
                System.out.println(jsonResponse);

                JSONObject schemaJson = new JSONObject(new JSONTokener(
                                Objects.requireNonNull(getClass()
                                                .getResourceAsStream("/json schemas/JSON_Schema_Reading.json"))));
                Schema schema = SchemaLoader.load(schemaJson);

                assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                JSONObject responseJson = new JSONObject(jsonResponse);
                assertDoesNotThrow(() -> schema.validate(responseJson),
                                "This JSON does not conform to the provided schema.");
        }

        @Test
        public void testUpdateReadingOk() throws SQLException {
                UUID mockReadingUuid = UUID.randomUUID();
                Reading mockReading = new Reading(mockReadingUuid, "This is a comment", mockCustomer, LocalDate.now(),
                                KindOfMeter.WASSER, 2.5, "12345", false);

                Mockito.doNothing().when(mockReadingDao).updateReading(mockReading);

                Response response = readingResource.updateReading(mockReading);

                assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
                assertEquals("Reading with uuid: " + mockReadingUuid + " was updated.", response.getEntity());
        }

        @Test
        public void testUpdateReadingNotFound() throws SQLException, JsonProcessingException {
                UUID mockReadingUuid = UUID.randomUUID();
                Reading mockReading = new Reading(mockReadingUuid, "This is a comment", mockCustomer, LocalDate.now(),
                                KindOfMeter.WASSER, 2.5, "12345", false);

                Mockito.doThrow(new SQLException()).when(mockReadingDao).updateReading(mockReading);

                Response response = readingResource.updateReading(mockReading);

                assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
                assertEquals("Could not find reading with uuid : " + mockReadingUuid, response.getEntity());
        }

        @Test
        public void testUpdateReadingNoUuid() throws SQLException, JsonProcessingException {
                Reading mockReading = new Reading(null, "This is a comment", mockCustomer, LocalDate.now(),
                                KindOfMeter.WASSER, 2.5, "12345", false);
                Mockito.doThrow(new SQLException()).when(mockReadingDao).updateReading(mockReading);

                Response response = readingResource.updateReading(mockReading);

                assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
                assertEquals("Please provide a valid uuid!", response.getEntity());
        }

        @Test
        public void testGetReadingsByParameter() throws SQLException, JsonProcessingException {
                Reading mockReading = new Reading(UUID.randomUUID(), "This is a comment", mockCustomer, LocalDate.now(),
                                KindOfMeter.WASSER, 2.5, "12345", false);
                when(mockReadingDao.getReadings(mockCustomer.getId(), LocalDate.of(2025, 1, 1),
                                LocalDate.of(3025, 12, 12),
                                KindOfMeter.WASSER)).thenReturn(List.of(mockReading));

                Response response = readingResource.getReadingsByParameter(mockCustomer.getId().toString(),
                                "2025-01-01",
                                "3025-12-12", "wAsSeR");

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(response.getEntity());

                JSONObject schemaJson = new JSONObject(new JSONTokener(
                                Objects.requireNonNull(getClass()
                                                .getResourceAsStream("/json schemas/JSON_Schema_Readings.json"))));
                Schema schema = SchemaLoader.load(schemaJson);

                assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                System.out.println(jsonResponse);
                JSONObject responseJson = new JSONObject(jsonResponse);
                assertDoesNotThrow(() -> schema.validate(responseJson),
                                "This JSON does not conform to the provided schema.");
        }

        @Test
        public void testGetReadingsByParameterNoCustomerId() throws SQLException, JsonProcessingException {
                Response response = readingResource.getReadingsByParameter(null, null, null, null);

                assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
                assertEquals("customerId is not defined", response.getEntity());
        }

        @Test
        public void testGetReadingsByParameterWrongStartDateFOrmat() throws SQLException, JsonProcessingException {
                UUID randomUuid = UUID.randomUUID();
                Response response = readingResource.getReadingsByParameter(randomUuid.toString(), "2025.01.01", null,
                                null);

                assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
                assertEquals("Invalid start format", response.getEntity());
        }

        @Test
        public void testGetReadingsByParameterWrongEndDateFOrmat() throws SQLException, JsonProcessingException {
                UUID randomUuid = UUID.randomUUID();
                Response response = readingResource.getReadingsByParameter(randomUuid.toString(), null, "2025.01.01",
                                null);

                assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
                assertEquals("Invalid end format", response.getEntity());
        }
}
