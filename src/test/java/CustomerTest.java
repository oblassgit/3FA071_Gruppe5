import dev.hv.model.Customer;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    static UUID customerId;

    @BeforeEach
    public void before() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));
        databaseCon.openConnections(properties);
        customerId = UUID.randomUUID();
        customer = new Customer(customerId, "Schwanzus", "Longus", LocalDate.of(2005, 6, 9), Gender.M);
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
    public void testToString() {
        assertEquals(customer.toString(), "Customer{firstName='Schwanzus', lastName='Longus', birthDate="
                + LocalDate.of(2005, 6, 9) + ", gender=" + Gender.M + ", id=" + customerId + '}');
    }

    @AfterAll
    public static void after() {
        databaseCon.truncateAllTables();
        databaseCon.closeConnections();

    }

}
