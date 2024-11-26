package dev.hv.rest.resource;

import dev.hv.enums.Gender;
import dev.hv.model.Customer;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.util.UUID;

@Path("customers")
public class CustomerResource {

    @Path("get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomer() {
        return new Customer(UUID.randomUUID(), "Hans", "Wurst", LocalDate.now(), Gender.M);
    }
}

// @PathParam("uuid") String uuid
