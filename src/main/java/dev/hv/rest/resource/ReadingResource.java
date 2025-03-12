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
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Path("readings")
public class ReadingResource {

    private Util util = new Util();
    private DatabaseCon databaseCon = new DatabaseCon();
    private ReadingDao readingDao;

    // constructor for jackson
    public ReadingResource() throws SQLException {
        databaseCon = new DatabaseCon();
        databaseCon.openConnections(util.getProperties());
        readingDao = new ReadingDao(databaseCon.getConnection());
    }

    // dependency injection for testing
    public ReadingResource(DatabaseCon databaseCon, ReadingDao readingDao) {
        this.databaseCon = databaseCon;
        this.readingDao = readingDao;
    }

    @Path("/{uuid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReading(@PathParam("uuid") String uuid) {

        Reading reading;

        try {
            databaseCon.getConnection();
            databaseCon.createAllTables();

            reading = readingDao.getReading(UUID.fromString(uuid));

        } catch (SQLException e) {
            return Response.serverError().build();
        }

        return Response.ok(reading).build();
    }

    @Path("/{uuid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReading(@PathParam("uuid") String uuidRaw) {
        Reading reading;
        try {
            UUID uuid = UUID.fromString(uuidRaw);

            databaseCon.getConnection();
            databaseCon.createAllTables();

            reading = readingDao.getReading(uuid);
            if (reading == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Could not find reading with uuid : " + uuid).build();
            }

            readingDao.deleteReading(reading);

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.ok(reading).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createReading(Reading input) {
        if (input == null || input.getCustomer() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid body.").build();
        }

        databaseCon.getConnection();
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

            readingDao.createReading(input);

            return Response.status(Response.Status.CREATED).entity(input).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateReading(Reading input) {
        databaseCon.getConnection();
        if (input.getId() != null) {
            try {
                readingDao.updateReading(input);
                return Response.ok().entity("Reading with uuid: " + input.getId() + " was updated.").build();
            } catch (SQLException e) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Could not find reading with uuid : " + input.getId()).build();

            }
        } else
            return Response.status(Response.Status.BAD_REQUEST).entity("Please provide a valid uuid!").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadingsByParameter(
            @QueryParam("customer") String cutstomerIdRaw,
            @QueryParam("start") String startDateRaw,
            @QueryParam("end") String endDateRaw,
            @QueryParam("kindOfMeter") String kindOfMeterRaw) {

        HashMap<String, List<Reading>> returnObject = new HashMap<String, List<Reading>>();
        try {
            UUID customerId = null;
            LocalDate startDate = null;
            LocalDate endDate = null;
            KindOfMeter kindOfMeter = null;
            List<Reading> readings = new ArrayList<>();
            try {

                if (kindOfMeterRaw != null) {
                    kindOfMeter = KindOfMeter.valueOf(kindOfMeterRaw.toUpperCase());
                }

                String pattern = "yyyy-MM-dd";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                if (startDateRaw != null) {
                    if (util.validateDateTime(startDateRaw, pattern)) {
                        startDate = LocalDate.parse(startDateRaw, formatter);
                    } else {
                        throw new Exception("Invalid start format");
                    }
                }
                if (endDateRaw != null) {
                    if (util.validateDateTime(endDateRaw, pattern)) {
                        endDate = LocalDate.parse(endDateRaw, formatter);
                    } else {
                        throw new Exception("Invalid end format");
                    }
                }
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
            }

            databaseCon.getConnection();
            databaseCon.createAllTables();

            readings = readingDao.getReadings(customerId, startDate, endDate, kindOfMeter);

            returnObject.put("readings", readings);

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.ok(returnObject).build();
    }
}
