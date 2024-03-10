package com.train.ticket.Exception;

import lombok.Getter;

@Getter
public class TicketNotFoundException extends RuntimeException {
    private final APIError apiError;

    public TicketNotFoundException(APIError apiError) {
        super(apiError.getMessage());
        this.apiError = apiError;
    }
}
