package db;
import dev.hv.ExampleDataCreator;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.KindOfMeter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Properties;

public class ExampleDataCreatorTests {
    Properties properties;

    DatabaseCon databaseCon = new DatabaseCon();

    @BeforeEach
    public void before() throws IOException {
        properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));

        databaseCon.openConnections(properties);
        databaseCon.createAllTables();
        databaseCon.truncateAllTables();

    }

    @Test
    public void testCreateCustomersFromCSV () throws SQLException, IOException {
        ResultSet resultSet;

        ExampleDataCreator.createCustomersFromCSV("src/main/resources/csv/kunden_utf8.csv");

        String selectStatement = "SELECT COUNT(*) AS recordCount FROM Customer";

        resultSet = databaseCon.getConnection().createStatement().executeQuery(selectStatement);
        resultSet.next();
        assertEquals(1001, resultSet.getInt("recordCount"));

    }

    @Test
    public void testCreateReadingsFromCSV() throws SQLException, IOException, ParseException {
        ResultSet resultSet;
        System.out.println(" >>>>>> testCreateReadingsFromCSV ");

        ExampleDataCreator.createCustomersFromCSV("src/main/resources/csv/kunden_utf8.csv");
        ExampleDataCreator.createReadingsFromCsv("src/main/resources/csv/wasser.csv", KindOfMeter.WASSER);

        String selectStatement = "SELECT COUNT(*) AS recordCount FROM Reading";

        resultSet = databaseCon.getConnection().createStatement().executeQuery(selectStatement);
        resultSet.next();
        assertEquals(66, resultSet.getInt("recordCount"));
    }
}
