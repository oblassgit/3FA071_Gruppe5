import dev.hv.model.Customer;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.Reading;
import dev.hv.db.ReadingDao;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

public class ReadingDaoTest {

    static DatabaseCon databaseCon = new DatabaseCon();

    static Customer customer;

    static CustomerDao customerDao;
    static ReadingDao readingDao;

    @BeforeClass
    public static void before() throws IOException, SQLException {

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
    public void testCreateReading() throws SQLException, IOException {
        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0,
                "id", false);
        readingDao.createReading(reading);
        assertEquals(readingDao.getReading(reading.getId()).getId(), reading.getId()); 

    }

    @Test
    public void testUpdateReading() throws SQLException, IOException {
        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0,
                "id", false);
        readingDao.createReading(reading);
        Reading updatedReading = new Reading(reading.getId(), "comment", customer, LocalDate.now(), KindOfMeter.WASSER,
                12.0, "id", false);
        readingDao.updateReading(updatedReading);
        assertEquals(readingDao.getReading(updatedReading.getId()).getKindOfMeter(), KindOfMeter.WASSER);
    }

    @Test
    public void testGetReading() throws SQLException, IOException {
        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0,
                "id", false);
        readingDao.createReading(reading);
        readingDao.getReading(reading.getId());
        assertEquals(readingDao.getReading(reading.getId()).getId(), reading.getId());
    }

    @Test
    public void testCreateReadingWithoutCustomer() throws SQLException, IOException {
        UUID test = UUID.randomUUID();
        Reading reading = new Reading(test, "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0, "id", false);

        readingDao.createReading(reading);
        assertNotEquals(customerDao.getCustomer(customer.getId()), null);
        assertNotEquals( readingDao.getReading(test),null);

    }

    @AfterClass
    public static void after() {
        databaseCon.truncateAllTables();
        databaseCon.closeConnections();
    }
}
