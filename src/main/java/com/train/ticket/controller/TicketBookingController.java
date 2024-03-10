package com.train.ticket.controller;

import com.train.ticket.model.Seat;
import com.train.ticket.model.Ticket;
import com.train.ticket.model.TicketRequest;
import com.train.ticket.service.TicketBookingService;
import com.train.ticket.validation.TicketValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tickets")
public class TicketBookingController {
    @Autowired
    TicketBookingService ticketBookingService;

    @Autowired
    TicketValidation ticketValidation;

    @PostMapping("/purchase")
    public ResponseEntity<Ticket> purchaseTicket(@RequestBody TicketRequest ticketRequest,
                                                 BindingResult result) throws BindException {
        ticketValidation.validate(ticketRequest, result);
        if (result.hasErrors()) {
            throw new BindException(result);
        }

        Ticket bookedTicket = ticketBookingService.purchaseTicket(ticketRequest);
        return new ResponseEntity<>(bookedTicket, HttpStatus.CREATED);
    }

    @GetMapping("/receipt/{email}")
    public ResponseEntity<Ticket> getReceipt(@PathVariable String email) {
        Ticket ticket = ticketBookingService.getReceipt(email);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Seat>> getUsersAndSeats(@RequestParam String section) {
        List<Seat> userAndSeats = ticketBookingService.getUserAndSeats(section);
        return new ResponseEntity<>(userAndSeats, HttpStatus.OK);
    }

    @DeleteMapping("/remove/{email}")
    public void removeUser(@PathVariable String email) {
        ticketBookingService.removeUser(email);
    }

    @PutMapping("/modify/{email}")
    public ResponseEntity<Ticket> modifySeat(@PathVariable String email, @RequestParam String newSection) {
        Ticket ticket = ticketBookingService.modifySeat(email, newSection);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }
}
