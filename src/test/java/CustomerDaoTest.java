import org.group5.Customer;
import org.group5.CustomerDao;
import org.group5.DatabaseCon;
import org.group5.Gender;
import org.junit.AfterClass;
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


    @BeforeClass
    public static void before() throws IOException, SQLException {

        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));

        databaseCon.openConnections(properties);
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

    @AfterClass
    public static void after() {
        databaseCon.closeConnections();
    }
}
