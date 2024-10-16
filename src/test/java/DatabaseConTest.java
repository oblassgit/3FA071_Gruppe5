import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;
import org.group5.Customer;
import org.group5.CustomerDao;
import org.group5.DatabaseCon;
import org.group5.Gender;
import org.junit.Before;
import org.junit.Test;

public class DatabaseConTest {
    Properties properties;
    PreparedStatement statement;
    ResultSet resultSet;

    @Before
    public void before() throws IOException {
        properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));
    }

    @Test
    public void testDbCon() throws SQLException {
        DatabaseCon dbCon = new DatabaseCon();

        dbCon.openConnections(properties);
        Connection con = dbCon.getConnection();
        assert con.isClosed() == false;

        dbCon.createAllTables();
        statement = con.prepareStatement("select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_TYPE = 'BASE TABLE' and table_schema = '" + properties.getProperty("db_name")+"'");
        resultSet = statement.executeQuery();
        assert resultSet.next();

        Customer customer = new Customer(UUID.randomUUID(), "Peter", "Griffon", LocalDate.now(), Gender.M);
        CustomerDao customerDao = new CustomerDao(dbCon.getConnection());
        customerDao.createCustomer(customer);
        assert customerDao.getAllCustomers().size() > 0;

        dbCon.truncateAllTables();
        assert customerDao.getAllCustomers().size() == 0;

        dbCon.removeAllTables();
        resultSet = statement.executeQuery();
        assert resultSet.next() == false;

        dbCon.closeConnections();
        con = dbCon.getConnection();
        assert con.isClosed();
    }
}
