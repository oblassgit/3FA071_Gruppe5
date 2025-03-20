package db;
import dev.hv.model.Customer;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.Reading;
import dev.hv.db.ReadingDao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerDaoTest {

    static DatabaseCon databaseCon = new DatabaseCon();

    static Customer customer;
    static CustomerDao customerDao;


    @BeforeEach
    public void before() throws IOException, SQLException {

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
        Customer newCustomer = new Customer(UUID.randomUUID(), "Oliver", "Blass", LocalDate.now(), Gender.M);

        customerDao.createCustomer(newCustomer);
    }

    @Test
    public void testGetCustomer() throws SQLException {
        assertEquals(customer.getId(), customerDao.getCustomer(customer.getId()).getId());
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

        assertEquals(customerDao.getAllCustomers().size(), 4);
    }

    @Test
    public void testUpdateCustomer() throws SQLException {
        assertEquals(customerDao.getCustomer(customer.getId()).getGender(), Gender.M);
        Customer updatedCustomer = new Customer(customer.getId(), "Sigrid", "Blass", customer.getBirthDate(), Gender.W);
        customerDao.updateCustomer(updatedCustomer);

        assertEquals(customerDao.getCustomer(customer.getId()).getGender(), Gender.W);
    }

    @Test
    public void testDeleteCustomer() throws SQLException {
        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0, "id", false);

        ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());
        readingDao.createReading(reading);

        customerDao.deleteCustomer(customer);
        assert readingDao.getReading(reading.getId()).getCustomer() == null;
        assertNull(readingDao.getReading(reading.getId()).getCustomer());
    }

    @Test
    public void testGetCustomersByDateRange() throws SQLException {
        // Arrange
        Customer customer1 = new Customer(UUID.randomUUID(), "Alice", "Smith", LocalDate.of(1985, 5, 20), Gender.W);
        Customer customer2 = new Customer(UUID.randomUUID(), "Bob", "Brown", LocalDate.of(1990, 7, 15), Gender.M);

        customerDao.createCustomer(customer1);
        customerDao.createCustomer(customer2);

        // Act
        List<Customer> result = customerDao.getCustomers(LocalDate.of(1980, 1, 1), LocalDate.of(1989, 12, 31), null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(customer1.getId(), result.get(0).getId());
    }

    @Test
    public void testGetCustomersByGender() throws SQLException {
        // Arrange
        Customer customer1 = new Customer(UUID.randomUUID(), "Alice", "Smith", LocalDate.of(1985, 5, 20), Gender.W);
        Customer customer2 = new Customer(UUID.randomUUID(), "Bob", "Brown", LocalDate.of(1990, 7, 15), Gender.M);

        customerDao.createCustomer(customer1);
        customerDao.createCustomer(customer2);

        // Act
        List<Customer> result = customerDao.getCustomers(null, null, Gender.W);

        // Assert
        assertEquals(1, result.size());
        assertEquals(customer1.getId(), result.getFirst().getId());
    }

    @Test
    public void testGetReadingsForCustomer() throws SQLException {
        Reading reading = new Reading(UUID.randomUUID(), "comment", customer, LocalDate.now(), KindOfMeter.STROM, 12.0, "id", false);

        ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());
        readingDao.createReading(reading);

        customerDao.getReadingsForCustomer(customer);
        assertNotNull(customerDao.getReadingsForCustomer(customer));
        assertEquals(customerDao.getReadingsForCustomer(customer).size(), 1);
    }

    @AfterAll
    public static void after() {
        databaseCon.truncateAllTables();
        databaseCon.closeConnections();
    }
}
