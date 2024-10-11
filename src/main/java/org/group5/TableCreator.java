package org.group5;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class TableCreator {

    public static void createCustomerTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Customer ("
                + "id UUID NOT NULL, "
                + "first_name VARCHAR(100) NOT NULL, "
                + "last_name VARCHAR(100) NOT NULL, "
                + "birth_date DATE NOT NULL, "
                + "gender ENUM('D', 'M', 'U', 'W') NOT NULL, "
                + "PRIMARY KEY (id)"
                + ")";

        try (Connection connection = Util.getConnection("DbData");
             Statement statement = connection.createStatement()) {

            statement.execute(createTableSQL);
            System.out.println("Tabelle 'Customer' wurde erfolgreich erstellt.");

        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen der Tabelle Customer .");
            e.printStackTrace();
        }
    }
    public static void createReadingTable(){   
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Reading ("
                + "id UUID NOT NULL,"
                + "customer UUID NOT NULL,"
                + "comment VARCHAR(1000) NOT NULL,"
                + "date_of_reading TIMESTAMP NOT NULL,"
                + "meter_count INT NOT NULL,"
                + "meter_id VARCHAR(100) NOT NULL," //todo: find some way to do the enum in sql
                + "substitute BOOL NOT NULL,"
                + "FOREIGN KEY (customer) references Customer(id),"
                + "PRIMARY KEY (id)"
                + ")";

            try (Connection connection = Util.getConnection("DbData");
             Statement statement = connection.createStatement()) {

            statement.execute(createTableSQL);
            System.out.println("Tabelle 'Customer' wurde erfolgreich erstellt.");

        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen der Tabelle Reading.");
            e.printStackTrace();
        }

    }

}
