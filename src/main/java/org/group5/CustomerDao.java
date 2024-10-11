package org.group5;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class CustomerDao implements ICustomer {

    private UUID id;

    private Connection connection = Util.getConnection("DbData");


    public CustomerDao(UUID id, String firstName, String lastName, LocalDate birthDate, Gender gender) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("insert into Customer (id, first_name, last_name, birth_date, gender) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, id.toString());
        statement.setString(2, firstName);
        statement.setString(3, lastName);
        statement.setDate(4, Date.valueOf(birthDate));
        statement.setString(5, gender.toString());
        statement.execute();

        this.id = id;
    }

    public void deleteCustomer() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("delete from Customer where id = ?");
        statement.setString(1, id.toString());

        statement.execute();
    }


    @Override
    public void setFirstName(String firstName) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("update Customer set first_name = ? where id = ?");
            statement.setString(1, firstName);
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setLastName(String lastName) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("update Customer set last_name = ? where id = ?");
            statement.setString(1, lastName);
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setBirthDate(LocalDate birthDate) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("update Customer set birth_date = ? where id = ?");
            statement.setDate(1, Date.valueOf(birthDate));
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setGender(Gender gender) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("update Customer set gender = ? where id = ?");
            statement.setString(1, gender.toString());
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getFirstName() {
        PreparedStatement statement;
        String name = "";

        try {
            statement = connection.prepareStatement("select first_name from Customer where id = ?");
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                name = resultSet.getString("first_name");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return name;

    }

    @Override
    public String getLastName() {
        PreparedStatement statement;
        String name = "";

        try {
            statement = connection.prepareStatement("select last_name from Customer where id = ?");
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                name = resultSet.getString("last_name");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return name;
    }

    @Override
    public LocalDate getBirthDate() {
        PreparedStatement statement;
        LocalDate birthDate = null;

        try {
            statement = connection.prepareStatement("select birth_date from Customer where id = ?");
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                birthDate = resultSet.getDate("birth_date").toLocalDate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return birthDate;

    }

    @Override
    public Gender getGender() {
        PreparedStatement statement;
        Gender gender = Gender.U;

        try {
            statement = connection.prepareStatement("select gender from Customer where id = ?");
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                gender = Gender.valueOf(resultSet.getString("gender"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return gender;

    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }
}
