import dev.hv.model.Customer;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CustomerTest {

    static DatabaseCon databaseCon = new DatabaseCon();
    static Customer customer;

    @BeforeEach
    public void before() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));
        databaseCon.openConnections(properties);
        customer = new Customer(UUID.randomUUID(), "Schwanzus", "Longus", LocalDate.of(2005, 6, 9), Gender.M);
        databaseCon.removeAllTables();
        databaseCon.createAllTables();
        databaseCon.truncateAllTables();
    }

    @Test
    public void testSettersAndGetters() {
        LocalDate TestDate = LocalDate.now();

        customer.setFirstName("Skidadel");
        customer.setLastName("Skidudel");
        customer.setBirthDate(TestDate);
        customer.setGender(Gender.D);

        assertEquals("Skidadel", customer.getFirstName());
        assertEquals("Skidudel", customer.getLastName());
        assertEquals(TestDate, customer.getBirthDate());
        assertEquals(Gender.D, customer.getGender());

    }

    @Test
    public void testEqualsPositive() {
        Customer equalCustomer = new Customer(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getBirthDate(), customer.getGender());
        assertEquals(customer, equalCustomer);
    }

    @Test
    public void testNotEqualsNegative() {
        Customer differentCustomer = new Customer(UUID.randomUUID(), "Fritz", "Walter", LocalDate.now(), Gender.M);
        assertNotEquals(customer, differentCustomer);
    }

    @AfterAll
    public static void after() {
        databaseCon.truncateAllTables();
        databaseCon.closeConnections();

    }

}
