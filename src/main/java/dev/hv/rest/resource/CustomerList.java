package dev.hv.rest.resource;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import dev.hv.model.Customer;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo
        .Id.NAME)
@JsonTypeName(value="customers")
public class CustomerList extends ArrayList {

    public CustomerList(List<Customer> customers) {
        addAll(customers);
    }
    
}
