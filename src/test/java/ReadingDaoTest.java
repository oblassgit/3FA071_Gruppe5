import org.group5.Customer;
import org.group5.CustomerDao;
import org.group5.DatabaseCon;
import org.group5.Gender;
import org.group5.KindOfMeter;
import org.group5.Reading;
import org.group5.ReadingDao;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileReader;
import java.io.IO;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

public class ReadingDaoTest {


    static DatabaseCon databaseCon = new DatabaseCon();


    @BeforeClass
    public static void before() throws IOException {

        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));

        databaseCon.openConnections(properties);
        databaseCon.createAllTables();
        databaseCon.truncateAllTables();

    }

    @Test
    public void testCreateReading() throws SQLException, IOException {
        Customer customer = new Customer(UUID.randomUUID(), "Oliver", "Blass", LocalDate.now(), Gender.M);

        CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());
        customerDao.createCustomer(customer);

        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0, "id", false);

        ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());
        readingDao.createReading(reading);


        assert readingDao.getReading(reading.getId()).getId() == reading.getId();

    }

    @Test
    public void testUpdateReading() throws SQLException, IOException {
        Customer customer = new Customer(UUID.randomUUID(), "Donald", "Duck", LocalDate.now(), Gender.M);
        
        CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());
        customerDao.createCustomer(customer);

        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0, "id", false);

        ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());
        readingDao.createReading(reading);

        Reading updatedReading = new Reading(reading.getId(), "comment", customer, LocalDate.now(), KindOfMeter.WASSER, 12.0, "id", false);

        readingDao.updateReading(updatedReading);

        assert readingDao.getReading(updatedReading.getId()).getKindOfMeter() == KindOfMeter.WASSER;
    }

    @Test
    public void testGetReading() throws SQLException, IOException {
        Customer customer = new Customer(UUID.randomUUID(), "Donald", "Duck", LocalDate.now(), Gender.M);

        CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());
        customerDao.createCustomer(customer);

        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0, "id", false);

        ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());
        readingDao.createReading(reading);

        readingDao.getReading(reading.getId());

        assert readingDao.getReading(reading.getId()).getId() == reading.getId();

    }

    @AfterClass
    public static void after() {
        databaseCon.closeConnections();
    }
}
