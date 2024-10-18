import dev.hv.model.Customer;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.Reading;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.time.LocalDate;
import java.util.UUID;

public class ReadingTest {
    static DatabaseCon databaseCon = new DatabaseCon();
    static Customer customer;

    static Reading reading;
    static UUID testId = UUID.randomUUID();
    static LocalDate testDate = LocalDate.now();

    @BeforeEach
    public void before() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));

        databaseCon.openConnections(properties);
        databaseCon.removeAllTables();
        databaseCon.createAllTables();
        databaseCon.truncateAllTables();

        customer = new Customer(UUID.randomUUID(), "Stechus", "Kaktus", LocalDate.now(), Gender.M);

        reading = new Reading(testId, "testComment", customer, testDate, KindOfMeter.WASSER, 5.5, "meterId", true);
    }

    @Test
    public void testGetters() {
        assertEquals(reading.getId(), testId);
        assertEquals(reading.getComment(), "testComment");
        assertEquals(reading.getCustomer(), customer);
        assertEquals(reading.getDateOfReading(), testDate);
        assertEquals(reading.getKindOfMeter(), KindOfMeter.WASSER);
        assertEquals(reading.getMeterCount().toString(), "5.5");
        assertEquals(reading.getMeterId(), "meterId");
        assertTrue(reading.getSubstitute());
    }

    @Test
    public void testSetters() {
        UUID newTestId = UUID.randomUUID();
        LocalDate newTestDate = LocalDate.now().plusMonths(1);
        Customer newCustomer = new Customer(UUID.randomUUID(), "Ronald", "McDonald", LocalDate.now(), Gender.M);

        reading.setId(newTestId);
        assertEquals(reading.getId(), newTestId);
        reading.setComment("testComment");
        assertEquals(reading.getComment(), "testComment");
        reading.setCustomer(newCustomer);
        assertEquals(reading.getCustomer(), newCustomer);
        reading.setDateOfReading(newTestDate);
        assertEquals(reading.getDateOfReading(), newTestDate);
        reading.setKindOfMeter(KindOfMeter.STROM);
        assertEquals(reading.getKindOfMeter(), KindOfMeter.STROM);
        reading.setMeterCount(1.234);
        assertEquals(reading.getMeterCount().toString(), "1.234");
        reading.setMeterId("newMeterId");
        assertEquals(reading.getMeterId(), "newMeterId");
        reading.setSubstitute(false);
        assertFalse(reading.getSubstitute());
    }

    @Test
    public void testPrintDateOfReading() {
        assertEquals(reading.printDateOfReading(), testDate.toString());
    }

    @Test
    public void testToSring() {
        assertEquals(reading.toString(),
                "Reading{" + "id=" + testId + ", comment='testComment', customer=" + customer.toString()
                        + ", dateOfReading=" + testDate + ", kindOfMeter=" + KindOfMeter.WASSER
                        + ", meterCount=5.5, meterId='meterId', substitute=true}");
    }

    @AfterAll
    public static void after() {
        databaseCon.truncateAllTables();
        databaseCon.closeConnections();
    }
}