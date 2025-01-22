package dev.hv.db;

import dev.hv.Util;
import dev.hv.model.interfaces.IDatabaseConnections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseCon implements IDatabaseConnections {
    private Connection connection;
    private final static String createCustomerTable = """
            CREATE TABLE IF NOT EXISTS customer (
            id UUID NOT NULL,
            first_name VARCHAR(100) NOT NULL,
            last_name VARCHAR(100) NOT NULL,
            birth_date DATE,
            gender ENUM('D', 'M', 'U', 'W') NOT NULL,
            PRIMARY KEY (id)
            )
            """;

    private final static String createReadingTable = """
            CREATE TABLE IF NOT EXISTS reading (
            id UUID NOT NULL,
            customer_id UUID,
            comment VARCHAR(1000) NOT NULL,
            date_of_reading DATE NOT NULL,
            meter_count INT NOT NULL,
            meter_id VARCHAR(100) NOT NULL,
            kind_of_meter ENUM('HEIZUNG', 'STROM', 'WASSER', 'UNBEKANNT'),
            substitute BOOL NOT NULL,
            CONSTRAINT customer_fk FOREIGN KEY (customer_id) references customer(id),
            PRIMARY KEY (id)
            )
            """;

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public IDatabaseConnections openConnections(Properties properties) {
        
        Util util = new Util();

        try {
            // Verbindung zur MariaDB
            connection = DriverManager.getConnection(util.getDbUrl(), util.getDbUser(), util.getDbPw());
            System.out.println("Verbindung erfolgreich hergestellt.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Öffnen der Datenbankverbindung. \n" + util.getDbUser() + "\n" + util.getDbUrl());
        }
        return this;
    }

    @Override
    public void createAllTables() {
        try (Statement stmt = connection.createStatement()) {
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
            stmt.execute("TRUNCATE  TABLE reading;");
            stmt.execute("ALTER TABLE reading DROP CONSTRAINT customer_fk;");
            stmt.execute("TRUNCATE TABLE customer;");
            stmt.execute(
                    "ALTER TABLE reading ADD CONSTRAINT customer_fk FOREIGN KEY (customer_id) REFERENCES customer (id);");
            System.out.println("Alle Tabellen erfolgreich geleert.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Leeren der Tabellen.");
        }
    }

    @Override
    public void removeAllTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS reading;");
            stmt.execute("DROP TABLE IF EXISTS customer;");
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