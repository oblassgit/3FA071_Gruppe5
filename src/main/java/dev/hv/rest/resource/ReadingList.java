package dev.hv.rest.resource;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import dev.hv.model.Customer;
import dev.hv.model.Reading;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(value = "readings")
public class ReadingList {
    private List<Reading> readings;

    public ReadingList(List<Reading> readings) {
        this.readings = readings;
    }

    public ReadingList () {

    }

    public List<Reading> getReadings() {
        return readings;
    }

    public void addAll(List<Reading> readings) {
        this.readings = readings;
    }
}
