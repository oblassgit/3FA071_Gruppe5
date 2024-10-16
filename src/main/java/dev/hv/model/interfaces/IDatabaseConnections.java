package dev.hv.model.interfaces;

import java.sql.Connection;
import java.util.Properties;

public interface IDatabaseConnections {

    public Connection getConnection();

    public IDatabaseConnections openConnections(Properties properties);

    public void createAllTables();

    public void truncateAllTables();

    public void removeAllTables();

    public void closeConnections();
    
}
