package org.group5;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class ReadingDao implements IReading {
    private UUID id;
    private Connection connection = Util.getConnection("DbData");

    public ReadingDao(UUID id, String comment, ICustomer customer, LocalDate dateOfReading, KindOfMeter kindOfMeter,
            Double meterCount, String meterId, Boolean substitute) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "insert into Reading (id, comment, customer_id, date_of_reading, kind_of_meter, meter_count, meter_id, substitute) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, id.toString());
        statement.setString(2, comment);
        statement.setString(3, customer.getId().toString());
        statement.setDate(4, Date.valueOf(dateOfReading));
        statement.setString(5, kindOfMeter.toString());
        statement.setDouble(6, meterCount);
        statement.setString(7, meterId);
        statement.setBoolean(8, substitute);
        statement.execute();

        this.id = id;
    }

    public void deleteCustomer() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("delete from Reading where id = ?");
        statement.setString(1, id.toString());

        statement.execute();
    }

    @Override
    public void setComment(String comment) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("update Reading set comment = ? where id = ?");
            statement.setString(1, comment);
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public void setCustomer(ICustomer customer) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("update Reading set customer_id = ? where id = ?");
            statement.setString(1, customer.getId().toString());
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public void setDateOfReading(LocalDate dateOfReading) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("update Reading set date_of_reading = ? where id = ?");
            statement.setDate(1, Date.valueOf(dateOfReading));
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public void setKindOfMeter(KindOfMeter kindOfMeter) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("update Reading set kind_of_meter = ? where id = ?");
            statement.setString(1, kindOfMeter.toString());
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public void setMeterCount(Double meterCount) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("update Reading set meter_count = ? where id = ?");
            statement.setDouble(1, meterCount);
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public void setMeterId(String meterId) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("update Reading set meter_id = ? where id = ?");
            statement.setString(1, meterId);
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public void setSubstitute(Boolean substitute) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("update Reading set substitute = ? where id = ?");
            statement.setBoolean(1, substitute);
            statement.setString(2, id.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public String getComment() {
        PreparedStatement statement;
        String comment = "";

        try {
            statement = connection.prepareStatement("select comment from Reading where id = ?");
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                comment = resultSet.getString("comment");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return comment;
    };

    @Override
    public ICustomer getCustomer() {
        PreparedStatement statement;
        ICustomer customer = null;

        try {
            statement = connection.prepareStatement(
                    "select c.* from Customer c join Reading r on c.id = r.customer_id where r.id = ?");
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                customer = resultSet.getObject("", ICustomer.class);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return customer;
    };

    @Override
    public LocalDate getDateOfReading() {
        PreparedStatement statement;
        LocalDate dateOfReading = null;

        try {
            statement = connection.prepareStatement("select date_of_reading from Reading where id = ?");
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                dateOfReading = resultSet.getDate("date_of_reading").toLocalDate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dateOfReading;
    };

    @Override
    public KindOfMeter getKindOfMeter() {
        PreparedStatement statement;
        KindOfMeter kindOfMeter = null;

        try {
            statement = connection.prepareStatement("select kind_of_meter from Reading where id = ?");
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                kindOfMeter = KindOfMeter.valueOf(resultSet.getString("kind_of_meter"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return kindOfMeter;
    };

    @Override
    public Double getMeterCount() {
        PreparedStatement statement;
        Double meterCount = null;

        try {
            statement = connection.prepareStatement("select meter_count from Reading where id = ?");
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                meterCount = resultSet.getDouble("meter_count");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return meterCount;
    };

    @Override
    public String getMeterId() {
        PreparedStatement statement;
        String meterId = null;

        try {
            statement = connection.prepareStatement("select meter_id from Reading where id = ?");
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                meterId = resultSet.getString("meter_id");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return meterId;
    };

    @Override
    public Boolean getSubstitute() {
        PreparedStatement statement;
        Boolean substitute = null;

        try {
            statement = connection.prepareStatement("select substitute from Reading where id = ?");
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                substitute = resultSet.getBoolean("substitute");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return substitute;
    };

    @Override
    public String printDateOfReading() {
        String dateOfReadingString = getDateOfReading().toString();
        System.out.println(dateOfReadingString);
        return dateOfReadingString;
    };

    @Override
    public UUID getId() {
        return this.id;
    };

    @Override
    public void setId(UUID id) {
        this.id = id;
    };
}
