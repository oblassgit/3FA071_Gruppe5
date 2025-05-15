package dev.hv.model;

public class CustomerWrapper {

    private Customer customer;

    public CustomerWrapper() {}

    public CustomerWrapper(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
