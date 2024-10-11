package org.group5;
import java.sql.Connection; 
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseCon implements IDatabaseConnections {
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    @Override
    public IDatabaseConnections openConnections(Properties properties) {

        final String dburl = properties.getProperty("db_url");
        final String dbuser = properties.getProperty("db_user");
        final String dbpw = properties.getProperty("db_pw");

        try {
            // Verbindung zur MariaDB 
            connection = DriverManager.getConnection(dburl, dbuser, dbpw);
            System.out.println("Verbindung erfolgreich hergestellt.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Öffnen der Datenbankverbindung.");
        }
        return this;
    }

    @Override
    public void createAllTabes() {
        try (Statement stmt = connection.createStatement()) {
      String createCustomerTable = "CREATE TABLE IF NOT EXISTS Customer ("
                + "id UUID NOT NULL, "
                + "first_name VARCHAR(100) NOT NULL, "
                + "last_name VARCHAR(100) NOT NULL, "
                + "birth_date DATE NOT NULL, "
                + "gender ENUM('D', 'M', 'U', 'W') NOT NULL, "
                + "PRIMARY KEY (id)"
                + ")";
                
                String createReadingTable = "CREATE TABLE IF NOT EXISTS Reading ("
                + "id UUID NOT NULL,"
                + "customer UUID NOT NULL,"
                + "comment VARCHAR(1000) NOT NULL,"
                + "date_of_reading TIMESTAMP NOT NULL,"
                + "meter_count INT NOT NULL,"
                + "meter_id VARCHAR(100) NOT NULL,"
                + "kind_of_meter ENUM('HEIZUNG', 'STROM', 'WASSER', 'UNBEKANNT'),"
                + "substitute BOOL NOT NULL,"
                + "FOREIGN KEY (customer) references Customer(id),"
                + "PRIMARY KEY (id)"
                + ")";
            stmt.execute(createCustomerTable);
            stmt.execute(createReadingTable);
            System.out.println("Alle Tabellen erfolgreich erstellt.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Erstellen der Tabellen.");
        }
    }

    @Override
    public void truncateAllTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0; " +
                    "TRUNCATE TABLE Reading; ");
            stmt.execute("TRUNCATE TABLE Customer;");
            System.out.println("Alle Tabellen erfolgreich geleert.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Leeren der Tabellen.");
        }
    }

    @Override
    public void removeAllTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Reading;");
            stmt.execute("DROP TABLE IF EXISTS Customer;");
            System.out.println("Alle Tabellen erfolgreich entfernt.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Entfernen der Tabellen.");
        }
    }

    @Override
    public void closeConnections() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Verbindung erfolgreich geschlossen.");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Fehler beim Schließen der Datenbankverbindung.");
            }
        }
    }
}
