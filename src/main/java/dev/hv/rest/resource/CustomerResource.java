package dev.hv.rest.resource;

import dev.hv.Util;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("customers")
public class CustomerResource {

    private final Util util = new Util();

    private final DatabaseCon databaseCon;
    private final CustomerDao customerDao;

    // constructor for jackson
    public CustomerResource() throws SQLException {
        databaseCon = new DatabaseCon();
        databaseCon.openConnections(util.getProperties());
        customerDao = new CustomerDao(databaseCon.getConnection());
    }

    // dependency injection for testing
    public CustomerResource(DatabaseCon databaseCon, CustomerDao customerDao) {
        this.databaseCon = databaseCon;
        this.customerDao = customerDao;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() {
        CustomerList customerList;


        try {
            databaseCon.openConnections(util.getProperties());
            databaseCon.createAllTables();

            customerList = new CustomerList(customerDao.getAllCustomers());

        } catch (SQLException e) {
            return Response.serverError().build();
        }
        
        return Response.ok(customerList).build();
    }

    @Path("/{uuid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(@PathParam("uuid") String uuid) {

        Customer customer;

        try {
            databaseCon.openConnections(util.getProperties());
            databaseCon.createAllTables();

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

        List<Reading> readings;

        try {
            databaseCon.openConnections(util.getProperties());
            databaseCon.createAllTables();

            customer = customerDao.getCustomer(UUID.fromString(uuid));
            if (customer != null) {
                readings = customerDao.getReadingsForCustomer(customer);
                customerDao.deleteCustomer(customer);

                for (Reading reading:
                        readings) {
                    reading.setCustomer(null);
                }

            } else {
                return Response.status(404).entity("Customer with uuid: " + uuid + "cannot be found.").build();
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCustomer(Customer customer) {


        databaseCon.openConnections(util.getProperties());

        // Prüfen, ob eine UUID vorhanden ist, sonst eine generieren
        if (customer.getId() == null) {
            customer.setId(UUID.randomUUID());
        }

        try {
            customerDao.createCustomer(customer);
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }



        // Response mit 201 und der gespeicherten Entität zurückgeben
        return Response.status(Response.Status.CREATED)
                .entity(customer)
                .build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateCustomer(Customer customer) {

        databaseCon.openConnections(util.getProperties());

        if (customer.getId() != null) {
            try {
                customerDao.updateCustomer(customer);
                return Response.ok().entity("Customer with uuid: " + customer.getId() + " was updated.").build();
            } catch (SQLException e) {
                return Response.status(Response.Status.NOT_FOUND).entity("Could not find customer with uuid: " + customer.getId()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Please provide a valid uuid!").build();
        }


    }

}

