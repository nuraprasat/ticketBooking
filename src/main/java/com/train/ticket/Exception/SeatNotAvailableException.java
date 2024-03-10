package com.train.ticket.Exception;

import lombok.Getter;

@Getter
public class SeatNotAvailableException extends RuntimeException {
    private final APIError apiError;

    public SeatNotAvailableException(APIError apiError) {
        super(apiError.getMessage());
        this.apiError = apiError;
    }
}
