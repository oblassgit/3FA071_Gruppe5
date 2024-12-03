package dev.hv.rest.resource;

import dev.hv.Util;
import dev.hv.db.DatabaseCon;
import dev.hv.db.ReadingDao;
import dev.hv.model.Reading;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.UUID;

@Path("reading")
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

       
        // Customer customer = new Customer(UUID.randomUUID(), "Hans", "Wurst", LocalDate.now(), Gender.M);
        // Reading reading2 = new Reading(UUID.randomUUID(), "testComment", customer, LocalDate.now(), KindOfMeter.WASSER, 5.5, "meterId", true);

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

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response putReading(Reading input) {

        try {
            databaseCon.openConnections(util.getProperties());
            databaseCon.createAllTables();

            ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());

            if (input == null) {
                return Response.serverError().build();
            } else
                readingDao.updateReading(input);
                
        } catch (SQLException e) {
            return Response.serverError().build();
        }

        return Response.ok(input).build();

    }

}

}
