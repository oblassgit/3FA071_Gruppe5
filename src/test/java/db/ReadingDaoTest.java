package db;

import dev.hv.model.Customer;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.Reading;
import dev.hv.db.ReadingDao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class ReadingDaoTest {

    static DatabaseCon databaseCon = new DatabaseCon();

    static Customer customer;

    static CustomerDao customerDao;
    static ReadingDao readingDao;

    @BeforeEach
    public void before() throws IOException, SQLException {

        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));

        databaseCon.openConnections(properties);
        databaseCon.createAllTables();
        databaseCon.truncateAllTables();

        customer = new Customer(UUID.randomUUID(), "Donald", "Duck", LocalDate.now(), Gender.M);
        customerDao = new CustomerDao(databaseCon.getConnection());
        customerDao.createCustomer(customer);
        readingDao = new ReadingDao(databaseCon.getConnection());

    }

    @Test
    public void testCreateReading() throws SQLException {
        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0,
                "id", false);
        readingDao.createReading(reading);
        assertEquals(readingDao.getReading(reading.getId()).getId(), reading.getId());

    }

    @Test
    public void testUpdateReading() throws SQLException {
        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0,
                "id", false);
        readingDao.createReading(reading);
        Reading updatedReading = new Reading(reading.getId(), "comment", customer, LocalDate.now(), KindOfMeter.WASSER,
                12.0, "id", false);
        readingDao.updateReading(updatedReading);
        assertEquals(readingDao.getReading(updatedReading.getId()).getKindOfMeter(), KindOfMeter.WASSER);
    }

    @Test
    public void testGetReading() throws SQLException {
        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0,
                "id", false);
        readingDao.createReading(reading);
        readingDao.getReading(reading.getId());
        assertEquals(readingDao.getReading(reading.getId()).getId(), reading.getId());
    }

    @Test
    public void testDeleteReading() throws SQLException {
        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0,
                "id", false);
        readingDao.createReading(reading);
        readingDao.deleteReading(reading);
        Reading deletedReading = readingDao.getReading(reading.getId());

        assert deletedReading == null;
    }

    @Test
    public void testCreateReadingWithoutCustomer() throws SQLException {
        UUID test = UUID.randomUUID();
        Reading reading = new Reading(test, "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0, "id", false);

        readingDao.createReading(reading);
        assertNotEquals(customerDao.getCustomer(customer.getId()), null);
        assertNotEquals(readingDao.getReading(test), null);

    }

    @Test
    public void testGetReadings() throws SQLException {
        Reading reading0 = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.WASSER,
                12.0, "id", false);
        Reading reading1 = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0,
                "id", false);
        Reading reading2 = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now().plusDays(-365),
                KindOfMeter.WASSER, 12.0, "id", false);
        Reading reading3 = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now().plusDays(365),
                KindOfMeter.WASSER, 12.0, "id", false);
        readingDao.createReading(reading0);
        readingDao.createReading(reading1);
        readingDao.createReading(reading2);
        readingDao.createReading(reading3);

        List<Reading> foundReadings = readingDao.getReadings(customer.getId(), LocalDate.now().plusDays(-3),
                LocalDate.now().plusDays(3), KindOfMeter.WASSER);
        List<Reading> expectedReadings = List.of(reading0);

        assertEquals(expectedReadings, foundReadings);
    }

    @Test
    public void testCreateReadings() throws SQLException {
        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0,
                "id", false);
        Reading reading1 = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0,
                "id", false);

        List list = new ArrayList();
        list.add(reading);
        list.add(reading1);

        readingDao.createReadings(list);
        List readingsInDB = readingDao.getReadings(null, null, null, null);
        assertEquals(2, readingsInDB.size());
    }

    @AfterAll
    public static void after() {
        databaseCon.truncateAllTables();
        databaseCon.closeConnections();
    }
}
