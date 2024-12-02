package dev.hv.rest.resource;

import dev.hv.Util;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.model.Customer;
import dev.hv.model.Reading;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("customers")
public class CustomerResource {

    private Util util = new Util();
    private DatabaseCon databaseCon = new DatabaseCon();


    @Path("/{uuid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(@PathParam("uuid") String uuid) {

        Customer customer;

        try {
            databaseCon.openConnections(util.getProperties());
            databaseCon.createAllTables();

            CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());
            customer = customerDao.getCustomer(UUID.fromString(uuid));

        } catch (SQLException e) {
            return Response.status(404).build();
        }

        //Customer customer = new Customer(UUID.randomUUID(), "Hans", "Wurst", LocalDate.now(), Gender.M);
        return Response.ok(customer).build();
    }

    @Path("/{uuid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("uuid") String uuid) {
        Customer customer;

        List<Reading> readings = new ArrayList<>();

        try {
            databaseCon.openConnections(util.getProperties());
            databaseCon.createAllTables();
            databaseCon.truncateAllTables();
            CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());


            customer = customerDao.getCustomer(UUID.fromString(uuid));
            readings = customerDao.getReadingsForCustomer(customer);
            customerDao.deleteCustomer(customer);

            for (Reading reading:
                 readings) {
                reading.setCustomer(null);
                System.out.println(reading);
            }

        } catch (SQLException e) {
            return Response.status(404).build();
        }

        ReadingList readingList = new ReadingList(readings);

        Map<String, Object> response = new HashMap<>();
        response.put("customer", customer);
        response.put("readings", readingList);

        return Response.ok(response).build();
    }
}

//

