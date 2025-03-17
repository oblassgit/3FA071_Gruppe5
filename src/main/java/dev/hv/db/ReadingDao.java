package dev.hv.db;

import dev.hv.model.Customer;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.Reading;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReadingDao {

    Connection connection;

    PreparedStatement createStatement;
    PreparedStatement updateStatement;
    PreparedStatement deleteStatement;
    PreparedStatement getStatement;

    public ReadingDao(Connection connection) throws SQLException {
        this.connection = connection;
        createStatement = connection.prepareStatement(
                "insert into reading (id, comment, customer_id, date_of_reading, kind_of_meter, meter_count, meter_id, substitute) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        updateStatement = connection.prepareStatement("update reading set comment = ?, customer_id = ?, " +
                "date_of_reading = ?, kind_of_meter = ?, meter_count = ?, meter_id = ?, substitute = ? where id = ?");
        deleteStatement = connection.prepareStatement("delete from reading where id = ?");
        getStatement = connection.prepareStatement("select * from reading where id = ?");
    
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

            createStatement.setString(1, reading.getId().toString());
            createStatement.setString(2, reading.getComment());
            createStatement.setString(3, reading.getCustomer().getId().toString());
            createStatement.setDate(4, Date.valueOf(reading.getDateOfReading()));
            createStatement.setString(5, reading.getKindOfMeter().toString());
            createStatement.setDouble(6, reading.getMeterCount());
            createStatement.setString(7, reading.getMeterId());
            createStatement.setBoolean(8, reading.getSubstitute());
            createStatement.execute();

            Statement commitTransactionStatement = connection.createStatement();
            commitTransactionStatement.executeQuery("commit");
        } catch (SQLException e) {
            Statement rollbackStatement = connection.createStatement();
            rollbackStatement.executeQuery("rollback");
            System.err.println("Transaction failed! Rolled back changes.");
            System.err.println(createStatement.toString() + ": FAILED!");
            e.printStackTrace();
        }

    }

    public void updateReading(Reading reading) throws SQLException {
        updateStatement.setString(1, reading.getComment());
        updateStatement.setString(2, reading.getCustomer().getId().toString());
        updateStatement.setDate(3, Date.valueOf(reading.getDateOfReading()));
        updateStatement.setString(4, reading.getKindOfMeter().toString());
        updateStatement.setDouble(5, reading.getMeterCount());
        updateStatement.setString(6, reading.getMeterId());
        updateStatement.setBoolean(7, reading.getSubstitute());
        updateStatement.setString(8, reading.getId().toString());
        updateStatement.executeUpdate();
    }

    public void deleteReading(Reading reading) throws SQLException {
        deleteStatement.setString(1, reading.getId().toString());
        deleteStatement.execute();
    }

    public Reading getReading(UUID id) throws SQLException {
        getStatement.setString(1, String.valueOf(id));
        ResultSet resultSet = getStatement.executeQuery();

        if (resultSet.next()) {
            Customer customer = null;

            if (resultSet.getString("customer_id") != null) {
                customer = new CustomerDao(connection).getCustomer(UUID.fromString(resultSet.getString("customer_id")));
            }
            return new Reading(id, resultSet.getString("comment"), customer,
                    resultSet.getDate("date_of_reading").toLocalDate(),
                    KindOfMeter.valueOf(resultSet.getString("kind_of_meter")), resultSet.getDouble("meter_count"),
                    resultSet.getString("meter_id"), resultSet.getBoolean("substitute"));
        }

        return null;
    }

    public List<Reading> getReadings(UUID customerId, LocalDate startDate, LocalDate endDate, KindOfMeter kindOfMeter) throws SQLException {
        List<Reading> readings = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM reading WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (customerId != null) {
            queryBuilder.append(" AND customer_id = ?");
            parameters.add(customerId.toString());
        }
        if (kindOfMeter != null) {
            queryBuilder.append(" AND kind_of_meter = ?");
            parameters.add(kindOfMeter.name());
        }
        if (startDate != null) {
            queryBuilder.append(" AND date_of_reading > ?");
            parameters.add(Date.valueOf(startDate));
        }
        if (endDate != null) {
            queryBuilder.append(" AND date_of_reading < ?");
            parameters.add(Date.valueOf(endDate));
        }

        try (PreparedStatement stmt = connection.prepareStatement(queryBuilder.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    UUID readingId = UUID.fromString(resultSet.getString("id"));
                    String comment = resultSet.getString("comment");
                    LocalDate dateOfReading = resultSet.getDate("date_of_reading").toLocalDate();
                    KindOfMeter meterType = KindOfMeter.valueOf(resultSet.getString("kind_of_meter"));
                    double meterCount = resultSet.getDouble("meter_count");
                    String meterId = resultSet.getString("meter_id");
                    boolean substitute = resultSet.getBoolean("substitute");
                    Customer customer;
                    String rsCustomerId = resultSet.getString("customer_id");
                    if (rsCustomerId == null || rsCustomerId.isEmpty()) {
                        customer = null;
                    } else {
                        customer = new CustomerDao(connection).getCustomer(UUID.fromString(resultSet.getString("customer_id")));
                    }
                    readings.add(new Reading(readingId, comment, customer, dateOfReading, meterType, meterCount, meterId, substitute));
                }
            }
        }

        return readings;
    }

}