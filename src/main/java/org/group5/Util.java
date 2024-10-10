package org.group5;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {

    // singelton
    private static Connection con = null;

    // close
    public static void close(final AutoCloseable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (final Exception e) {
                // ignore
            }
        }
    }

    // factory methode
    public static Connection getConnection(final String db) {

        if (con == null) {
            try {
                final Properties prop = new Properties();
                prop.load(new FileReader("src/main/resources/" + db + ".properties"));
                final String dburl = prop.getProperty("db_url");
                final String dbuser = prop.getProperty("db_user");
                final String dbpw = prop.getProperty("db_pw");

                con = DriverManager.getConnection(dburl, dbuser, dbpw);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return con;
    }
    private Util() {
    }
}
