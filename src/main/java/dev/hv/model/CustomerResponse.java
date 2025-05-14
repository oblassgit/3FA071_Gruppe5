package dev.hv.model;

public class CustomerResponse {


    private Customer customer;

    public CustomerResponse() {}

    public CustomerResponse (Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
