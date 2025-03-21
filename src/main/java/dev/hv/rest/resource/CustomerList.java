package dev.hv.rest.resource;

import java.util.List;

import dev.hv.model.Customer;

public class CustomerList {
    private List<Customer> customers;

    public CustomerList() {} // Default constructor (important for Jackson)

    public CustomerList(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}

