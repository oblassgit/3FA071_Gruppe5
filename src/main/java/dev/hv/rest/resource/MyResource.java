package dev.hv.rest.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("hello")
public class MyResource {
    
    @Path("World")
    @GET
    public String getHelloWorld() {
        return "hello World";
    }
}
