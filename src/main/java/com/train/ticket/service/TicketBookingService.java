package com.train.ticket.service;

import com.train.ticket.model.Seat;
import com.train.ticket.model.Ticket;
import com.train.ticket.model.TicketRequest;

import java.util.List;
import java.util.Map;

public interface TicketBookingService {
    Ticket purchaseTicket(TicketRequest ticketRequest);
    List<Seat> getUserAndSeats(String section);
    Ticket modifySeat(String email, String newSection);
    Ticket getReceipt(String email);
    void removeUser(String email);
}
