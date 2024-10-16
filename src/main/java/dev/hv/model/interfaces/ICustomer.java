package dev.hv.model.interfaces;

import dev.hv.enums.Gender;

import java.time.LocalDate;

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
