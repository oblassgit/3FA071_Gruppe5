package dev.hv.model;

public class ReadingWrapper {
    private Reading reading;

    public ReadingWrapper() {}

    public ReadingWrapper(Reading reading) {
        this.reading = reading;
    }

    public Reading getReading() {
        return reading;
    }

    public void setCustomer(Reading reading) {
        this.reading = reading;
    }
}
