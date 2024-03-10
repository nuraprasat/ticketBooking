package com.train.ticket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TicketRequest {
    private String from;
    private String to;
    private User user;
    private double price;
}
