package org.group5;

import java.time.LocalDate;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {

        UUID uuid = UUID.randomUUID();

        Customer customer = new Customer(uuid, "Oliver", "Blass", LocalDate.now(), Gender.M);
        System.out.println(customer.getFirstName() + " " + customer.getLastName());
        System.out.println(customer.getGender());


    }
}