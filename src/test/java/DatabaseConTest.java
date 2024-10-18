import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;
import dev.hv.model.Customer;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DatabaseConTest {
    static Properties properties;
    PreparedStatement statement;
    ResultSet resultSet;

    @BeforeEach
    public void before() throws IOException {
        properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));
    }

    @Test
    public  void testDbCon() throws SQLException {
        DatabaseCon dbCon = new DatabaseCon();

        dbCon.openConnections(properties);
        Connection con = dbCon.getConnection();
        assertFalse(con.isClosed());

        dbCon.createAllTables();
        statement = con.prepareStatement("select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_TYPE = 'BASE TABLE' and (TABLE_NAME = 'customer' OR TABLE_NAME = 'reading')");
        resultSet = statement.executeQuery();
        assertTrue(resultSet.next());

        Customer customer = new Customer(UUID.randomUUID(), "Peter", "Griffon", LocalDate.now(), Gender.M);
        CustomerDao customerDao = new CustomerDao(dbCon.getConnection());
        customerDao.createCustomer(customer);
        assertTrue(customerDao.getAllCustomers().size() > 0);

        dbCon.truncateAllTables();
        assertEquals(customerDao.getAllCustomers().size(), 0);

        dbCon.removeAllTables();
        resultSet = statement.executeQuery();
        assertFalse(resultSet.next());

        dbCon.closeConnections();
        con = dbCon.getConnection();
        assertTrue(con.isClosed());
    }
}
