package dev.hv.rest.resource;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dev.hv.Util;
import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.enums.Gender;
import dev.hv.model.Customer;
import dev.hv.model.Reading;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.StringWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public Response getCustomers(
            @QueryParam("start") String startDateRaw,
            @QueryParam("end") String endDateRaw,
            @QueryParam("gender") String genderRaw
    ) {

        CustomerList customerList = new CustomerList(Collections.emptyList());
        LocalDate startDate = null;
        LocalDate endDate = null;
        Gender gender = null;

        try {
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
            if (genderRaw != null) {
                gender = Gender.valueOf(genderRaw.toUpperCase());
            }

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        try {
            databaseCon.openConnections(util.getProperties());
            databaseCon.createAllTables();

            customerList.addAll(customerDao.getCustomers(startDate, endDate, gender));

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

        if (customer.getId() == null) {
            customer.setId(UUID.randomUUID());
        }

        try {
            customerDao.createCustomer(customer);
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

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

    @GET
    @Path("/export/csv")
    @Produces("text/csv")
    public Response exportCSV() {
        try {

            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = csvMapper.schemaFor(Customer.class).withHeader();
            StringWriter writer = new StringWriter();
            csvMapper.writer(schema).writeValue(writer, customerDao.getAllCustomers());

            return Response.ok(writer.toString())
                    .header("Content-Disposition", "attachment; filename=\"data.csv\"")
                    .type("text/csv")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error exporting CSV").build();
        }
    }

    @GET
    @Path("/export/xml")
    @Produces(MediaType.APPLICATION_XML)
    public Response exportXML() {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            StringWriter writer = new StringWriter();
            xmlMapper.writeValue(writer, customerDao.getAllCustomers());

            return Response.ok(writer.toString())
                    .header("Content-Disposition", "attachment; filename=\"data.xml\"")
                    .type("application/xml")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error exporting XML").build();
        }
    }

}

