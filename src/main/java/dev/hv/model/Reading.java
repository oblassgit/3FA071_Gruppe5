package dev.hv.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.interfaces.ICustomer;
import dev.hv.model.interfaces.IReading;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(value = "reading")
@JsonIgnoreProperties(ignoreUnknown = true)


public class Reading implements IReading {
    @JsonProperty("uuid")
    private UUID id;
    private String comment;
    private ICustomer customer;
    @JsonProperty("dateOfReading")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateOfReading;
    private KindOfMeter kindOfMeter;
    private Double meterCount;
    private String meterId;
    private Boolean substitute;   

    public Reading () {}

    public Reading (UUID id, String comment, ICustomer customer, LocalDate dateOfReading, KindOfMeter kindOfMeter, Double meterCount, String meterId, Boolean substitute) {
        this.id = id;
        this.comment = comment;
        this.customer = customer;
        this.dateOfReading = dateOfReading;
        this.kindOfMeter = kindOfMeter;
        this.meterCount = meterCount;
        this.meterId = meterId;
        this.substitute = substitute;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setCustomer(ICustomer customer) {
        this.customer = customer;
    }

    @Override
    public void setDateOfReading(LocalDate dateOfReading) {
        this.dateOfReading = dateOfReading;
    }

    @Override
    public void setKindOfMeter(KindOfMeter kindOfMeter) {
        this.kindOfMeter = kindOfMeter;
    }

    @Override
    public void setMeterCount(Double meterCount) {
        this.meterCount = meterCount;
    }

    @Override
    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    @Override
    public void setSubstitute(Boolean substitute) {
        this.substitute = substitute;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public ICustomer getCustomer() {
        return customer;
    }

    @Override
    public LocalDate getDateOfReading() {
        return dateOfReading;
    }

    @Override
    public KindOfMeter getKindOfMeter() {
        return kindOfMeter;
    }

    @Override
    public Double getMeterCount() {
        return meterCount;
    }

    @Override
    public String getMeterId() {
        return meterId;
    }

    @Override
    public Boolean getSubstitute() {
        return substitute;
    }

    @Override
    public String printDateOfReading() {
        return dateOfReading.toString();
    }

    @Override
    public String toString() {
        return "Reading{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", customer=" + customer +
                ", dateOfReading=" + dateOfReading +
                ", kindOfMeter=" + kindOfMeter +
                ", meterCount=" + meterCount +
                ", meterId='" + meterId + '\'' +
                ", substitute=" + substitute +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reading reading = (Reading) o;
        return id.equals(reading.id) && Objects.equals(comment, reading.comment) && customer.equals(reading.customer) && Objects.equals(dateOfReading, reading.dateOfReading) && kindOfMeter == reading.kindOfMeter && Objects.equals(meterCount, reading.meterCount) && Objects.equals(meterId, reading.meterId) && Objects.equals(substitute, reading.substitute);
    }
}
