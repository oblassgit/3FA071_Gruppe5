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

    public CustomerDao(Connection con) {
        connection = con;
    }

    public void createCustomer(Customer customer) throws SQLException {

        PreparedStatement statement = connection.prepareStatement(
                "insert into Customer (id, first_name, last_name, birth_date, gender) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, customer.getId().toString());
        statement.setString(2, customer.getFirstName());
        statement.setString(3, customer.getLastName());
        statement.setDate(4, Date.valueOf(customer.getBirthDate()));
        statement.setString(5, customer.getGender().toString());
        statement.execute();

    }

    public void deleteCustomer(Customer customer) throws SQLException {

        try {
            Statement stmt = connection.createStatement();
            stmt.executeQuery("start transaction");

            stmt.execute("ALTER TABLE reading DROP CONSTRAINT customer_fk;");

            PreparedStatement prepStmt = connection.prepareStatement("delete from Customer where id = ?");
            prepStmt.setString(1, customer.getId().toString());
            prepStmt.execute();

            stmt.execute(
                    "ALTER TABLE reading ADD CONSTRAINT customer_fk FOREIGN KEY (customer_id) REFERENCES customer (id);");

            prepStmt = connection
                    .prepareStatement("update Reading set customer_id = null where customer_id = ?");
            prepStmt.setString(1, customer.getId().toString());
            prepStmt.executeUpdate();

            stmt.executeQuery("commit");
        } catch (SQLException e) {
            Statement rollbackStmt = connection.createStatement();
            rollbackStmt.executeQuery("rollback");
            System.err.println("Transaction failed! Rolled back changes.");
            e.printStackTrace();
        }
    }

    public Customer getCustomer(UUID id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select * from Customer where id = ?");

        statement.setString(1, String.valueOf(id));
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new Customer(id, resultSet.getString("first_name"), resultSet.getString("last_name"),
                    resultSet.getDate("birth_date").toLocalDate(), Gender.valueOf(resultSet.getString("Gender")));
        }

        return null;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select * from Customer");

        ResultSet resultSet = statement.executeQuery();

        ArrayList<Customer> list = new ArrayList<>();

        while (resultSet.next()) {
            list.add(new Customer(UUID.fromString(resultSet.getString("id")), resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getDate("birth_date").toLocalDate(), Gender.valueOf(resultSet.getString("Gender"))));
        }

        return list;
    }

    public void updateCustomer(Customer customer) throws SQLException {
        PreparedStatement statement = connection
                .prepareStatement("update Customer set first_name = ?, last_name = ?, " +
                        "birth_date = ?, gender = ? where id = ?");

        statement.setString(1, customer.getFirstName());
        statement.setString(2, customer.getLastName());
        statement.setDate(3, Date.valueOf(customer.getBirthDate()));
        statement.setString(4, customer.getGender().toString());
        statement.setString(5, customer.getId().toString());

        statement.executeUpdate();
    }
}
