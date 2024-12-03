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
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private final Util util = new Util();
    private final DatabaseCon databaseCon = new DatabaseCon();


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() {
        CustomerList customerList;


        try {
            databaseCon.openConnections(util.getProperties());
            databaseCon.createAllTables();

            CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());
            customerList = new CustomerList(customerDao.getAllCustomers());

        } catch (SQLException e) {
            return Response.serverError().build();
        }

        if(customerList.isEmpty()) {
            System.out.println("Liste ist leer");
        } else {
            System.out.println("......");
            System.out.println(customerList.toString());
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

        List<Reading> readings;

        try {
            databaseCon.openConnections(util.getProperties());
            databaseCon.createAllTables();
            databaseCon.truncateAllTables();
            CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());


            customer = customerDao.getCustomer(UUID.fromString(uuid));
            if (customer != null) {
                readings = customerDao.getReadingsForCustomer(customer);
                customerDao.deleteCustomer(customer);

                for (Reading reading:
                        readings) {
                    reading.setCustomer(null);
                    System.out.println(reading);
                }

            } else {
                return Response.status(404).build();
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
            CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());
            customerDao.createCustomer(customer);
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }



        // Response mit 201 und der gespeicherten Entität zurückgeben
        return Response.status(Response.Status.CREATED)
                .entity(customer)
                .build();
    }

}

