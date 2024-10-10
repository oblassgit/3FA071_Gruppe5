package org.group5;

import java.time.LocalDate;

public interface IReading extends IID {
    public void setComment(String comment);

    public void setCustomer(ICustomer customer);

    public void setDateOfReading(LocalDate dateOfReading);

    public void setKindOfMeter(KindOfMeter kindOfMeter);

    public void setMeterCount(Double meterCount);

    public void setMeterId(String meterId);

    public void setSubstitute(Boolean substitute);

    public String getComment();

    public ICustomer getCustomer();

    public LocalDate getDateOfReading();

    public KindOfMeter getKindOfMeter();

    public Double getMeterCount();

    public String getMeterId();

    public Boolean getSubstitute();

    public String printDateOfReading();
}
