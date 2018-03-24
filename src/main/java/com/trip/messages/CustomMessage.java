package com.trip.messages;

import com.trip.model.Trip;

public class CustomMessage extends Trip {

    private String errorMessage;

    public CustomMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
