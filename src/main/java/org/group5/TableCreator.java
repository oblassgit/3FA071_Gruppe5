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
            System.out.println("Fehler beim Erstellen der Tabelle.");
            e.printStackTrace();
        }
    }
}
