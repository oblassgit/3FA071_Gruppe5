import dev.hv.model.Customer;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.Reading;
import dev.hv.db.ReadingDao;
import org.junit.AfterClass;
import org.junit.Before;
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
        Customer newCustomer = new Customer(UUID.randomUUID(), "Oliver", "Blass", LocalDate.now(), Gender.M);

        customerDao.createCustomer(newCustomer);
    }

    @Test
    public void testGetCustomer() throws SQLException {
        assert customerDao.getCustomer(customer.getId()).getId() == customer.getId();

    }

    @Test
    public void testGetAllCustomer() throws SQLException {
        databaseCon.truncateAllTables();

        Customer customer1 = new Customer(UUID.randomUUID(), "Herbert", "Zwerg", LocalDate.now(), Gender.M);
        Customer customer2 = new Customer(UUID.randomUUID(), "Hans", "Wurst", LocalDate.now(), Gender.M);
        Customer customer3 = new Customer(UUID.randomUUID(), "Marty", "McFly", LocalDate.now(), Gender.M);
        Customer customer4 = new Customer(UUID.randomUUID(), "Max", "Mustermann", LocalDate.now(), Gender.M);

        customerDao.createCustomer(customer1);
        customerDao.createCustomer(customer2);
        customerDao.createCustomer(customer3);
        customerDao.createCustomer(customer4);

        assert customerDao.getAllCustomers().size() == 4;
    }

    @Test
    public void testUpdateCustomer() throws SQLException {
        assert customerDao.getCustomer(customer.getId()).getGender() == Gender.M;
        Customer updatedCustomer = new Customer(customer.getId(), "Sigrid", "Blass", customer.getBirthDate(), Gender.W);
        customerDao.updateCustomer(updatedCustomer);

        assert customerDao.getCustomer(updatedCustomer.getId()).getGender() == Gender.W;
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
        databaseCon.truncateAllTables();
        databaseCon.closeConnections();
    }
}
