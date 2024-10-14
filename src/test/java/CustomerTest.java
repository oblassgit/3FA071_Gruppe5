import org.group5.Customer;
import org.group5.DatabaseCon;
import org.group5.Gender;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

public class CustomerTest {

    static DatabaseCon databaseCon = new DatabaseCon();
    static Customer customer;

    @Test
    public void Getters() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));
        databaseCon.openConnections(properties);

        customer = new Customer(UUID.randomUUID(), "Schwanzus", "Longus", LocalDate.of(2005, 6, 9), Gender.M);

        LocalDate.of(2005, 6, 9);
        customer.setFirstName("Skidadel");
        customer.setLastName("Skidudel");
        customer.setBirthDate(LocalDate.now());
        customer.setGender(Gender.D);

        assertEquals("Skidadel", customer.getFirstName());
        assertEquals("Skidudel", customer.getLastName());
        assertEquals(LocalDate.now(), customer.getBirthDate());
        assertEquals(Gender.D, customer.getGender());

    }

}
