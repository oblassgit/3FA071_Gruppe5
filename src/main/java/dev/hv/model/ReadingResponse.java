package dev.hv.model;

public class ReadingResponse {
    private Reading reading;

    public ReadingResponse() {}

    public ReadingResponse (Reading reading) {
        this.reading = reading;
    }

    public Reading getReading() {
        return reading;
    }

    public void setCustomer(Reading reading) {
        this.reading = reading;
    }
}
