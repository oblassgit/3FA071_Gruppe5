package org.group5;

import java.util.UUID;
import java.time.LocalDate;

public class ReadingDao implements IReading {
    public UUID id;
    public String comment;
    public ICustomer customer;
    public LocalDate dateOfReading;
    public KindOfMeter kindOfMeter;
    public Double meterCount;
    public String meterId;
    public Boolean substitute;

    public void setId(UUID id) {
        this.id = id;
    };

    public void setComment(String comment) {
        this.comment = comment;
    };

    public void setCustomer(ICustomer customer) {
        this.customer = customer;
    };

    public void setDateOfReading(LocalDate dateOfReading) {
        this.dateOfReading = dateOfReading;
    };

    public void setKindOfMeter(KindOfMeter kindOfMeter) {
        this.kindOfMeter = kindOfMeter;
    };

    public void setMeterCount(Double meterCount) {
        this.meterCount = meterCount;
    };

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    };

    public void setSubstitute(Boolean substitute) {
        this.substitute = substitute;
    };

    public UUID getId() {
        return this.id;
    };

    public String getComment() {
        return this.comment;
    };

    public ICustomer getCustomer() {
        return this.customer;
    };

    public LocalDate getDateOfReading() {
        return this.dateOfReading;
    };

    public KindOfMeter getKindOfMeter() {
        return this.kindOfMeter;
    };

    public Double getMeterCount() {
        return this.meterCount;
    };

    public String getMeterId() {
        return this.meterId;
    };

    public Boolean getSubstitute() {
        return this.substitute;
    };

    public String printDateOfReading() {
        System.out.println(this.dateOfReading.toString());
        return this.dateOfReading.toString();
    };
}
