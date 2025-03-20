package dev.hv.rest.resource;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import dev.hv.model.Customer;

/*@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo
        .Id.NAME)
@JsonTypeName(value="customers")
public class CustomerList extends ArrayList {

    public CustomerList(List<Customer> customers) {
        addAll(customers);
    }

    @JsonCreator
    public CustomerList(@JsonProperty("customers") List<Customer> customers) {
        this.customers = customers;
    }
    
    public CustomerList() {
    }

    
    
}*/

public class CustomerList {
    private List<Customer> customers;

    // No-Args-Konstruktor (wichtig für Jackson)
    public CustomerList() {
        this.customers = new ArrayList<>();
    }

    @JsonCreator
    public CustomerList(@JsonProperty("customers") List<Customer> customers) {
        this.customers = customers;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}
