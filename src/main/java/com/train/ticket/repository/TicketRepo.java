package com.train.ticket.repository;

import com.train.ticket.model.Ticket;

public interface TicketRepo {
    Ticket findByEmail(String email);
    Ticket save(String email, Ticket ticket);
    Ticket updateTicket(String email, Ticket ticket);
    void removeTicket(String email);
}
