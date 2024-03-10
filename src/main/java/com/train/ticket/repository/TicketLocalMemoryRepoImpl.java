package com.train.ticket.repository;

import com.train.ticket.model.Ticket;
import com.train.ticket.model.TrainSection;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TicketLocalMemoryRepoImpl implements TicketRepo {
    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();

    @Override
    public Ticket findByEmail(String email) {
        return tickets.get(email);
    }

    @Override
    public Ticket save(String email, Ticket ticket) {
        tickets.put(email, ticket);
        return ticket;
    }

    @Override
    public Ticket updateTicket(String email, Ticket ticket) {
        tickets.put(email, ticket);
        return ticket;
    }

    @Override
    public void removeTicket(String email) {
        tickets.remove(email);
    }
}
