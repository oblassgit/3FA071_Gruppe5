package org.group5;

import java.util.Properties;

public interface IDatabaseConnections {

    public IDatabaseConnections openConnections(Properties properties);

    public void createAllTabes();

    public void truncateAllTables();

    public void removeAllTables();

    public void closeConnections();
    
}
