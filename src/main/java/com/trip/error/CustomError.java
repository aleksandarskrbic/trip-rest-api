package com.trip.error;

import com.trip.model.Trip;

public class CustomError extends Trip {

    private String errorMessage;

    public CustomError(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
