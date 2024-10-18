package dev.hv;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Util {
    
    public void updateProperty(Properties properties) {
        
        String username = System.getProperty("user.name");
        String propertiesFilename = "src/main/resources/DbData.properties";

        final String dburl = properties.getProperty("db_url");
        final String dbuser = properties.getProperty("db_user");
        final String dbpw = properties.getProperty("db_pw");
        
        properties.setProperty(username + ".db.url", dburl);
        properties.setProperty(username + ".db.user", dbuser);
        properties.setProperty(username + ".db.pw", dbpw);
        
        try (FileOutputStream out = new FileOutputStream(propertiesFilename)) {
            properties.store(out, "Database Connection Properties");
            System.out.println("Properties file saved: " + propertiesFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        
    }
    


    

