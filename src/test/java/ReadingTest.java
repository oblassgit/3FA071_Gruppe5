import org.group5.Customer;
import org.group5.DatabaseCon;
import org.group5.Gender;
import org.group5.KindOfMeter;
import org.group5.Reading;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.time.LocalDate;
import java.util.UUID;

public class ReadingTest {
    static DatabaseCon databaseCon = new DatabaseCon();
    Customer customer;

    static Reading reading;
    UUID testId = UUID.randomUUID();
    LocalDate testDate = LocalDate.now();

    @Before
    public void before() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));

        databaseCon.openConnections(properties);
        databaseCon.removeAllTables();
        databaseCon.createAllTables();
        databaseCon.truncateAllTables();

        customer = new Customer(UUID.randomUUID(), "Stechus", "Kaktus", LocalDate.now(), Gender.M);

        reading = new Reading(testId, "testComment", customer, testDate, KindOfMeter.WASSER, 5.5, "meterId", true);
    }

    @Test
    public void testGetters() {
        assert reading.getId() == testId;
        assert reading.getComment() == "testComment";
        assert reading.getCustomer() == customer;
        assert reading.getDateOfReading() == testDate;
        assert reading.getKindOfMeter() == KindOfMeter.WASSER;
        assert reading.getMeterCount() == 5.5;
        assert reading.getMeterId() == "meterId";
        assert reading.getSubstitute();
    }

    @Test
    public void testSetters() {
        UUID newTestId = UUID.randomUUID();
        LocalDate newTestDate = LocalDate.now().plusMonths(1);
        Customer newCustomer = new Customer(UUID.randomUUID(), "Ronald", "McDonald", LocalDate.now(), Gender.M);


        reading.setId(newTestId);
        assert reading.getId() == newTestId;
        reading.setComment("testComment");
        assert reading.getComment() == "testComment";
        reading.setCustomer(newCustomer);
        assert reading.getCustomer() == newCustomer;
        reading.setDateOfReading(newTestDate);
        assert reading.getDateOfReading() == newTestDate;
        reading.setKindOfMeter(KindOfMeter.STROM);
        assert reading.getKindOfMeter() == KindOfMeter.STROM;
        reading.setMeterCount(1.234);
        assert reading.getMeterCount() == 1.234;
        reading.setMeterId("newMeterId");
        assert reading.getMeterId() == "newMeterId";
        reading.setSubstitute(false);
        assert reading.getSubstitute() == false;
    }

    @Test
    public void testPrintDateOfReading() {
        assert reading.printDateOfReading().equals(testDate.toString());
    }

    @AfterClass
    public static void after() {
        databaseCon.truncateAllTables();
        databaseCon.closeConnections();
    }
}