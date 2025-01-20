package dev.hv.model.interfaces;

import dev.hv.enums.Gender;
import dev.hv.model.Customer;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=Customer.class)
public interface ICustomer extends IID {
    public void setFirstName(String firstName);

    public void setLastName(String lastName);

    public void setBirthDate(LocalDate birthDate);

    public void setGender(Gender gender);

    public String getFirstName();

    public String getLastName();

    public LocalDate getBirthDate();

    public Gender getGender();
}
