package dev.hv.db;

import dev.hv.enums.KindOfMeter;
import dev.hv.model.Customer;
import dev.hv.enums.Gender;
import dev.hv.model.Reading;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerDao {

    private Connection connection;
    

    PreparedStatement createStatement;
    PreparedStatement deleteStatement;
    PreparedStatement removeCustomerInReadingStatement;
    PreparedStatement getStatement;
    PreparedStatement countStatement;
    PreparedStatement selectStatement;
    PreparedStatement updateStatement;
    PreparedStatement getReadingsForCustomerStatement;

    public CustomerDao(Connection con) throws SQLException {
        connection = con;
        createStatement = connection.prepareStatement(
                "insert into customer (id, first_name, last_name, birth_date, gender) VALUES (?, ?, ?, ?, ?)");
        deleteStatement = connection.prepareStatement("delete from customer where id = ?");
        removeCustomerInReadingStatement = connection
                .prepareStatement("update reading set customer_id = null where customer_id = ?");
        getStatement = connection.prepareStatement("select * from customer where id = ?");
        countStatement = connection.prepareStatement("select count(*) as count from customer");
        selectStatement = connection.prepareStatement("select * from customer");
        updateStatement = connection.prepareStatement("update customer set first_name = ?, last_name = ?, " +
                "birth_date = ?, gender = ? where id = ?");
        getReadingsForCustomerStatement = con.prepareStatement("select * from reading where customer_id = ?");
    }

    public void createCustomer(Customer customer) throws SQLException {
        createStatement.setString(1, customer.getId().toString());
        createStatement.setString(2, customer.getFirstName());
        createStatement.setString(3, customer.getLastName());
        createStatement.setDate(4, Date.valueOf(customer.getBirthDate()));
        createStatement.setString(5, customer.getGender().toString());
        createStatement.execute();

    }

    public void deleteCustomer(Customer customer) throws SQLException {

        try {
            Statement alterConstraintStatement = connection.createStatement();
            Statement transactionStatement = connection.createStatement();
            transactionStatement.executeQuery("start transaction");

            alterConstraintStatement.execute("ALTER TABLE reading DROP CONSTRAINT customer_fk;");

            deleteStatement.setString(1, customer.getId().toString());
            deleteStatement.execute();

            removeCustomerInReadingStatement.setString(1, customer.getId().toString());
            removeCustomerInReadingStatement.executeUpdate();

            alterConstraintStatement.execute(
                    "ALTER TABLE reading ADD CONSTRAINT customer_fk FOREIGN KEY (customer_id) REFERENCES customer (id);");
            
            transactionStatement.executeQuery("commit");
        } catch (SQLException e) {
            Statement rollbackStatement = connection.createStatement();
            rollbackStatement.executeQuery("rollback");
            System.err.println("Transaction failed! Rolled back changes.");
            e.printStackTrace();
        }
    }

    public Customer getCustomer(UUID id) throws SQLException {
        getStatement.setString(1, String.valueOf(id));
        ResultSet resultSet = getStatement.executeQuery();

        if (resultSet.next()) {
            return new Customer(id, resultSet.getString("first_name"), resultSet.getString("last_name"),
                    resultSet.getDate("birth_date").toLocalDate(), Gender.valueOf(resultSet.getString("Gender")));
        }

        return null;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        ResultSet countResultSet = countStatement.executeQuery();
        if (!countResultSet.next()) {
            return new ArrayList<>(0);
        }

        ResultSet resultSet = selectStatement.executeQuery();
        ArrayList<Customer> list = new ArrayList<>(countResultSet.getInt("count"));

        while (resultSet.next()) {
            list.add(new Customer(UUID.fromString(resultSet.getString("id")), resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getDate("birth_date").toLocalDate(), Gender.valueOf(resultSet.getString("Gender"))));
        }

        return list;
    }

    public List<Reading> getReadingsForCustomer(Customer customer) throws SQLException {

        getReadingsForCustomerStatement.setString(1, String.valueOf(customer.getId()));
        ResultSet resultSet = getReadingsForCustomerStatement.executeQuery();
        List<Reading> list = new ArrayList<>();

        while (resultSet.next()) {
            list.add(new Reading(resultSet.getObject("id", UUID.class), resultSet.getString("comment"), customer, resultSet.getDate("date_of_reading").toLocalDate(),
                    KindOfMeter.valueOf(resultSet.getString("kind_of_meter")), resultSet.getDouble("meter_count"),
                    resultSet.getString("meter_id"), resultSet.getBoolean("substitute")));
        }

        return list;
    }

    public void updateCustomer(Customer customer) throws SQLException {

        updateStatement.setString(1, customer.getFirstName());
        updateStatement.setString(2, customer.getLastName());
        updateStatement.setDate(3, Date.valueOf(customer.getBirthDate()));
        updateStatement.setString(4, customer.getGender().toString());
        updateStatement.setString(5, customer.getId().toString());
        updateStatement.executeUpdate();
    }
}
