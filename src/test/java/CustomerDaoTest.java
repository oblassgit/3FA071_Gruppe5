import org.group5.Customer;
import org.group5.CustomerDao;
import org.group5.DatabaseCon;
import org.group5.Gender;
import org.group5.KindOfMeter;
import org.group5.Reading;
import org.group5.ReadingDao;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

public class CustomerDaoTest {

    static DatabaseCon databaseCon = new DatabaseCon();

    static Customer customer;
    static CustomerDao customerDao;


    @Before
    public void before() throws IOException, SQLException {

        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));

        databaseCon.openConnections(properties);
        databaseCon.removeAllTables();
        databaseCon.createAllTables();
        databaseCon.truncateAllTables();

        customer = new Customer(UUID.randomUUID(), "Oliver", "Blass", LocalDate.now(), Gender.M);

        customerDao = new CustomerDao(databaseCon.getConnection());
        customerDao.createCustomer(customer);

    }

    @Test
    public void testCreateCustomer() throws SQLException {
        assert customerDao.getCustomer(customer.getId()).getId() == customer.getId();

    }

    @Test
    public void testDeleteCustomer() throws SQLException, IOException {
        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0, "id", false);

        ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());
        readingDao.createReading(reading);

        customerDao.deleteCustomer(customer);
        assert readingDao.getReading(reading.getId()).getCustomer() == null;
    }

    @AfterClass
    public static void after() {
        databaseCon.closeConnections();
    }
}
