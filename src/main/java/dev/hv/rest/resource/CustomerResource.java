package dev.hv.rest.resource;

import dev.hv.Util;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import dev.hv.model.Customer;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Path("customers")
public class CustomerResource {

    private Util util = new Util();
    private DatabaseCon databaseCon = new DatabaseCon();


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer() {
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
            return Response.serverError().build();
        }

        //Customer customer = new Customer(UUID.randomUUID(), "Hans", "Wurst", LocalDate.now(), Gender.M);
        return Response.ok(customer).build();
    }


}


//

