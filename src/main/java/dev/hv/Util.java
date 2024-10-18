package dev.hv;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Util {
    private String dburl;
    private String dbuser;
    private String dbpw;
    private Properties properties = new Properties();

    public Util() {
        try {

            FileInputStream fileInputStream = new FileInputStream("src/main/resources/DbData.properties");
            properties.load(fileInputStream);

            dburl = properties.getProperty(System.getProperty("user.name") + ".db.url");
            dbuser = properties.getProperty(System.getProperty("user.name") + ".db.user");
            dbpw = properties.getProperty(System.getProperty("user.name") + ".db.pw");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getDbUrl() {
        return dburl;
    }

    public String getDbUser() {
        return dbuser;
    }

    public String getDbPw() {
        return dbpw;
    }
}