package org.group5;

import java.time.LocalDate;

public interface ICustomer {
    public void setFirstName(String firstName);

    public void setLastName(String lastName);

    public void setBirthDate(LocalDate birthDate);

    public void setGender(Gender gender);

    public String getFirstName();

    public String getLastName();

    public LocalDate getBirthDate();

    public Gender getGender();
}
