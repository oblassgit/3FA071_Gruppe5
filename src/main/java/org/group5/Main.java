package org.group5;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws SQLException {

        UUID uuid = UUID.randomUUID();

        CustomerDao customerDao = new CustomerDao(uuid, "Oliver", "Blass", LocalDate.now(), Gender.M);
        System.out.println(customerDao.getFirstName() + " " + customerDao.getLastName());
        System.out.println(customerDao.getGender());

        customerDao.deleteCustomer();

    }
}