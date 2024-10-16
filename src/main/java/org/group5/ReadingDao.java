package org.group5;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class ReadingDao {

    Connection connection;

    public ReadingDao(Connection connection) {
        this.connection = connection;
    }

    public void createReading(Reading reading) throws SQLException {

        Customer customer = (Customer) reading.getCustomer();
        CustomerDao customerDao = new CustomerDao(connection);

        try {
            Statement startTransactionStatement = connection.createStatement();
            startTransactionStatement.executeQuery("start transaction");
            
            if (customerDao.getCustomer(customer.getId()) == null) {

                customerDao.createCustomer(customer);

            }
            PreparedStatement statement = connection.prepareStatement(
            "insert into Reading (id, comment, customer_id, date_of_reading, kind_of_meter, meter_count, meter_id, substitute) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, reading.getId().toString());
            statement.setString(2, reading.getComment());
            statement.setString(3, reading.getCustomer().getId().toString());
            statement.setDate(4, Date.valueOf(reading.getDateOfReading()));
            statement.setString(5, reading.getKindOfMeter().toString());
            statement.setDouble(6, reading.getMeterCount());
            statement.setString(7, reading.getMeterId());
            statement.setBoolean(8, reading.getSubstitute());
            statement.execute();

            Statement commitTransactionStatement = connection.createStatement();
            commitTransactionStatement.executeQuery("commit");
        } catch (SQLException e) {
            Statement rollbackStatement = connection.createStatement();
            rollbackStatement.executeQuery("rollback");
            System.err.println("Transaction failed! Rolled back changes.");
            e.printStackTrace();
        }

    }

    public void updateReading(Reading reading) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("update Reading set comment = ?, customer_id = ?, " +
                "date_of_reading = ?, kind_of_meter = ?, meter_count = ?, meter_id = ?, substitute = ? where id = ?");

        statement.setString(1, reading.getComment());
        statement.setString(2, reading.getCustomer().getId().toString());
        statement.setDate(3, Date.valueOf(reading.getDateOfReading()));
        statement.setString(4, reading.getKindOfMeter().toString());
        statement.setDouble(5, reading.getMeterCount());
        statement.setString(6, reading.getMeterId());
        statement.setBoolean(7, reading.getSubstitute());
        statement.setString(8, reading.getId().toString());




        statement.executeUpdate();
    }


    public Reading getReading(UUID id) throws SQLException, IOException {
        PreparedStatement statement = connection.prepareStatement("select * from Reading where id = ?");

        statement.setString(1, String.valueOf(id));
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            Customer customer = null;

            if (resultSet.getString("customer_id") != null) {
                customer = new CustomerDao(connection).getCustomer(UUID.fromString(resultSet.getString("customer_id")));
            }
            return new Reading(id, resultSet.getString("comment"), customer, resultSet.getDate("date_of_reading").toLocalDate(),
                    KindOfMeter.valueOf(resultSet.getString("kind_of_meter")), resultSet.getDouble("meter_count"),
                    resultSet.getString("meter_id"), resultSet.getBoolean("substitute"));
        }

        return null;
    }

}