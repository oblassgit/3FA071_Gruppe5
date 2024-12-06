package dev.hv.rest.resource;

import dev.hv.Util;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.db.ReadingDao;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.Customer;
import dev.hv.model.Reading;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("readings")
public class ReadingResource {

    private Util util = new Util();
    private DatabaseCon databaseCon = new DatabaseCon();

    
    @Path("/{uuid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReading(@PathParam("uuid") String uuid) {

        Reading reading;

        try {
            databaseCon.openConnections(util.getProperties());
            databaseCon.createAllTables();

            ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());
            reading = readingDao.getReading(UUID.fromString(uuid));

        } catch (SQLException e) {
            return Response.serverError().build();
        }

        return Response.ok(reading).build();
    }

    @Path("/{uuid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReading(@PathParam("uuid") String uuid) {
        Reading reading;
        try {
            databaseCon.openConnections(util.getProperties());
            databaseCon.createAllTables();

            ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());
            reading = readingDao.getReading(UUID.fromString(uuid));
            readingDao.deleteReading(reading);

        } catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok(reading).build();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createReading(Reading input) {
        if (input == null || input.getCustomer() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid body.").build();
        }

        databaseCon.openConnections(util.getProperties());
        databaseCon.createAllTables();

        try {
            Customer customer = (Customer) input.getCustomer();
            if (customer.getId() == null) {
                customer.setId(UUID.randomUUID());
                CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());
                customerDao.createCustomer(customer);
            }

            UUID id = input.getId() == null ? UUID.randomUUID() : input.getId();
            input.setId(id);

            ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());
            readingDao.createReading(input);

            return Response.status(Response.Status.CREATED).entity(id.toString()).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadingsByParameter(
            @QueryParam("customer") String cutstomerIdRaw,
            @QueryParam("start") String startDateRaw,
            @QueryParam("end") String endDateRaw,
            @QueryParam("kindOfMeter") String kindOfMeterRaw) {

        List<Reading> readings = new ArrayList<>();
        try {
            UUID customerId;
            LocalDate startDate = null;
            LocalDate endDate = null;
            KindOfMeter kindOfMeter = null;
            if (cutstomerIdRaw == null) {
                throw new Exception("customerId is not defined");
            } else {
                customerId = UUID.fromString(cutstomerIdRaw);
            }

            if (kindOfMeterRaw != null) {
                kindOfMeter = KindOfMeter.valueOf(kindOfMeterRaw);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (startDateRaw != null) {
                startDate = LocalDate.parse(startDateRaw, formatter);
            }
            if (endDateRaw != null) {
                endDate = LocalDate.parse(endDateRaw, formatter);
            }

            readings = getReadings(customerId, startDate, endDate, kindOfMeter);

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        return Response.ok(readings).build();
    }

    // Helpers
    private List<Reading> getReadings(UUID customerId, LocalDate startDate, LocalDate endDate,
            KindOfMeter kindOfMeter) throws Exception {
        List<Reading> readings = new ArrayList<>();

        String stmtString = "SELECT * FROM reading WHERE customer_id = ?";
        if (kindOfMeter != null) {
            stmtString = stmtString + " AND kind_of_meter = ?";
        }
        if (startDate != null) {
            stmtString = stmtString + " AND date_of_reading > ?";
        }
        if (endDate != null) {
            stmtString = stmtString + " AND date_of_reading < ?";
        }

        databaseCon.openConnections(util.getProperties());
        databaseCon.createAllTables();
        PreparedStatement stmt = databaseCon.getConnection().prepareStatement(stmtString);

        int Index = 1;
        stmt.setString(Index++, customerId.toString());
        if (kindOfMeter != null) {
            stmt.setString(Index++, kindOfMeter.toString());
        }
        if (startDate != null) {
            stmt.setString(Index++, startDate.toString());
        }
        if (endDate != null) {
            stmt.setString(Index++, endDate.toString());
        }

        ResultSet resultSet = stmt.executeQuery();

        CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());
        Customer customer = customerDao.getCustomer(customerId);

        while (resultSet.next()) {
            readings.add(new Reading(UUID.fromString(resultSet.getString("id")), resultSet.getString("comment"),
                    customer, resultSet.getDate("date_of_reading").toLocalDate(),
                    KindOfMeter.valueOf(resultSet.getString("kind_of_meter")), resultSet.getDouble("meter_count"),
                    resultSet.getString("meter_id"), resultSet.getBoolean("substitute")));
        }

        return readings;
    }
}