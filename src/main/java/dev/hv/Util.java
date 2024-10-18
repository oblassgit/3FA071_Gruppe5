package dev.hv;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Util {
    String dburl = "";
    String dbuser = "";
    String dbpw = "";

    public Properties getDbProperties() {
        String propertiesFilename = "src/main/resources/DbData.properties";
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(propertiesFilename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Properties properties = new Properties();
        try {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String dburl = properties.getProperty(System.getProperty("user.name") + ".db.url");
        final String dbuser = properties.getProperty(System.getProperty("user.name") + ".db.user");
        final String dbpw = properties.getProperty(System.getProperty("user.name") + ".db.pw");

        setDbUrl(dburl);
        setDbUser(dbuser);
        setDbPw(dbpw);
        
        return properties;
    }

    public void setDbUrl(String dburl) {
        this.dburl = dburl;
    }

    public String getDbUrl() {
        return dburl;
    }

    public void setDbUser(String dbuser) {
        this.dbuser = dbuser;
    }

    public String getDbUser() {
        return dbuser;
    }

    public void setDbPw(String dbpw) {
        this.dbpw = dbpw;
    }

    public String getDbPw() {
        return dbpw;
    }


        
    }
    


    

