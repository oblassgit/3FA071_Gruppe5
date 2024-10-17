package dev.hv.db;

import dev.hv.model.Customer;
import dev.hv.enums.Gender;

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
    PreparedStatement selectStatement;
    PreparedStatement updateStatement;
    
    public CustomerDao(Connection con) throws SQLException {
        connection = con;
        createStatement = connection.prepareStatement("insert into Customer (id, first_name, last_name, birth_date, gender) VALUES (?, ?, ?, ?, ?)");
        deleteStatement = connection.prepareStatement("delete from Customer where id = ?");
        removeCustomerInReadingStatement = connection.prepareStatement("update Reading set customer_id = null where customer_id = ?");
        selectStatement = connection.prepareStatement("select * from Customer");
        updateStatement = connection.prepareStatement("update Customer set first_name = ?, last_name = ?, " +
        "birth_date = ?, gender = ? where id = ?");
        getStatement = connection.prepareStatement("select * from Customer where id = ?");
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
            Statement startTransactionStatement = connection.createStatement();
            startTransactionStatement.executeQuery("start transaction");
            deleteStatement.setString(1, customer.getId().toString());
            deleteStatement.execute();

            removeCustomerInReadingStatement.setString(1, customer.getId().toString());
            removeCustomerInReadingStatement.executeUpdate();

            Statement commitTransactionStatement = connection.createStatement();
            commitTransactionStatement.executeQuery("commit");
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
        ResultSet resultSet = selectStatement.executeQuery();
        ArrayList<Customer> list = new ArrayList<>();

        while (resultSet.next()) {
            list.add(new Customer(UUID.fromString(resultSet.getString("id")), resultSet.getString("first_name"), resultSet.getString("last_name"),
                    resultSet.getDate("birth_date").toLocalDate(), Gender.valueOf(resultSet.getString("Gender"))));
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
