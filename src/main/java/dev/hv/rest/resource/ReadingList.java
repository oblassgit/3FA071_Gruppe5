package dev.hv.rest.resource;


import dev.hv.model.Reading;

import java.util.List;

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
