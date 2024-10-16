import dev.hv.model.Customer;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

public class CustomerTest {

    static DatabaseCon databaseCon = new DatabaseCon();
    static Customer customer;

    @Before
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

    @AfterClass
    public static void after() {
        databaseCon.truncateAllTables();
        databaseCon.closeConnections();

    }

}
