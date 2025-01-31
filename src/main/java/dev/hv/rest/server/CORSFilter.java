package dev.hv.rest.server;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Allow all origins (or specify specific domains instead of "*")
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");

        // Allow specific HTTP methods
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // Allow specific HTTP headers
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");

        // Allow credentials if needed
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        // Specify how long the results of a preflight request can be cached
        responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
    }
}