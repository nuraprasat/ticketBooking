package com.train.ticket.service;

import com.train.ticket.Exception.APIError;
import com.train.ticket.Exception.SeatNotAvailableException;
import com.train.ticket.Exception.TicketNotFoundException;
import com.train.ticket.model.*;
import com.train.ticket.repository.RepoStrategy;
import com.train.ticket.repository.TicketRepo;
import com.train.ticket.repository.TrainRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketBookingServiceImpl implements TicketBookingService {

    RepoStrategy repoStrategy;
    String TRAIN_ID = "Train1";

    @Autowired
    public TicketBookingServiceImpl(RepoStrategy repoStrategy) {
        this.repoStrategy = repoStrategy;
    }

    /**
     * Processes a ticket purchase request by allocating a seat on a train and saving the ticket information.
     * It then allocates a seat on this train for the user specified in the ticket request. A new ticket is created with the journey details and the allocated seat,
     * and finally, the ticket is saved using the user's email as a key.
     *
     * @param ticketRequest An object containing the details of the ticket request, including the user making the request,
     *                      the journey's start and end points, and the ticket price.
     * @return The saved ticket object, which includes the allocated seat and journey details. The ticket is saved
     *         in a repository that can be retrieved later using the user's email.
     */
    @Override
    public Ticket purchaseTicket(TicketRequest ticketRequest) {
        Train train = getTrainRepoStrategy().get(TRAIN_ID);
        Seat allocatedSeat = allocateSeat(train, ticketRequest.getUser());

        Ticket ticket = new Ticket();
        ticket.setFrom(ticketRequest.getFrom());
        ticket.setTo(ticketRequest.getTo());
        ticket.setPrice(ticketRequest.getPrice());
        ticket.setSeat(allocatedSeat);

        return getTicketRepoStrategy().save(ticketRequest.getUser().getEmail(), ticket);
    }

    @Override
    public List<Seat> getUserAndSeats(String section) {
        return getTrainRepoStrategy().getSeatBySection(section);
    }

    /**
     * Attempts to modify the seat assignment for a ticket associated with the specified email.
     * It checks for the availability of the new seat number, updates the ticket if available,
     * and frees the previously occupied seat. Throws exceptions if the ticket is not found or
     * the new seat is unavailable due to being occupied or non-existent.
     *
     * @param email The email address linked to the ticket to be modified.
     * @param newSeatNumber The new seat number to assign to the ticket.
     * @return The ticket updated with the new seat assignment.
     * @throws TicketNotFoundException if no ticket matches the provided email.
     * @throws SeatNotAvailableException if the requested new seat is occupied or does not exist.
     */
    @Override
    public Ticket modifySeat(String email, String newSeatNumber) {
        // Retrieve the ticket for the given email. Throw an exception if not found.
        Ticket ticket = getTicketRepoStrategy().findByEmail(email);
        if (ticket == null) {
            throw new TicketNotFoundException(new APIError("No ticket found for email: " + email));
        }

        Train train = getTrainRepoStrategy().get(TRAIN_ID);
        Optional<Seat> oldSeatOptional = findSeatByNumber(train, ticket.getSeat().getNumber());

        boolean seatAssigned = train.getSections().values().stream()
                .flatMap(section -> section.getSeats().stream())
                .anyMatch(seat -> {
                    if (seat.getNumber().equals(newSeatNumber) && seat.isOccupied()) {
                        return false;
                    } else if (seat.getNumber().equals(newSeatNumber)) {
                        seat.setOccupied(true);
                        seat.setUser(ticket.getSeat().getUser());
                        ticket.getSeat().setNumber(newSeatNumber);
                        return true;
                    }
                    return false;
                });

        if (!seatAssigned) {
            // If the new seat wasn't assigned for some reason (e.g., it doesn't exist or is already occupied),
            throw new SeatNotAvailableException(new APIError("Seat " + newSeatNumber + " is not available."));
        }

        // You might want to revert the old seat to occupied.
        oldSeatOptional.ifPresent(oldSeat -> oldSeat.setOccupied(false));
        return getTicketRepoStrategy().save(email, ticket);
    }

    @Override
    public Ticket getReceipt(String email) {
        Ticket ticket = getTicketRepoStrategy().findByEmail(email);
        if (ticket == null) {
            throw new TicketNotFoundException(new APIError("Ticket not found"));
        }

        return ticket;
    }

    @Override
    public void removeUser(String email) {
        Ticket ticket = getTicketRepoStrategy().findByEmail(email);
        Train train = getTrainRepoStrategy().get(TRAIN_ID);
        if (ticket == null) {
            throw new TicketNotFoundException(new APIError("Ticket not found"));
        }
        String oldSeatNumber = ticket.getSeat().getNumber();
        findSeatByNumber(train, oldSeatNumber).ifPresent(s -> s.setOccupied(false));
        getTicketRepoStrategy().removeTicket(email);
    }

    private Optional<Seat> findSeatByNumber(Train train, String seatNumber) {
        for (Section section : train.getSections().values()) {
            for (Seat seat : section.getSeats()) {
                if (seat.getNumber().equals(seatNumber)) {
                    return Optional.of(seat);
                }
            }
        }
        return Optional.empty();
    }

    private Seat allocateSeat(Train train, User user) {
        if (train != null) {
            for (Section section : train.getSections().values()) {
                Optional<Seat> seat = section.getNextAvailableSeat();
                if (seat.isPresent()) {
                    seat.get().setOccupied(true);
                    seat.get().setUser(user);
                    return seat.get();
                }
            }
        }
        throw new SeatNotAvailableException(new APIError("No available seats"));
    }

    public TicketRepo getTicketRepoStrategy() {
        return repoStrategy.getTicketRepoStrategyInstance(DBStrategy.LOCAL);
    }

    public TrainRepo getTrainRepoStrategy() {
        return repoStrategy.getTrainRepoStrategyInstance(DBStrategy.LOCAL);
    }
}
