package com.train.ticket.service;

import com.train.ticket.Exception.SeatNotAvailableException;
import com.train.ticket.Exception.TicketNotFoundException;
import com.train.ticket.model.*;
import com.train.ticket.repository.RepoStrategy;
import com.train.ticket.repository.TicketRepo;
import com.train.ticket.repository.TrainRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TicketBookingServiceImplTest {
    private RepoStrategy repoStrategyMock;
    private TicketRepo ticketRepo;
    private TrainRepo trainRepo;
    private TicketBookingServiceImpl ticketBookingService;
    private User user;

    @BeforeEach
    void setUp() {
        repoStrategyMock = mock(RepoStrategy.class);
        ticketRepo = mock(TicketRepo.class);
        trainRepo = mock(TrainRepo.class);
        ticketBookingService = new TicketBookingServiceImpl(repoStrategyMock);
        when(repoStrategyMock.getTicketRepoStrategyInstance(DBStrategy.LOCAL)).thenReturn(ticketRepo);
        when(repoStrategyMock.getTrainRepoStrategyInstance(DBStrategy.LOCAL)).thenReturn(trainRepo);
        user = new User("ar", "pr", "ar@gmail.com");
    }

    @Test
    void testPurchaseTicket_success() {
        User user = new User("ar", "pr", "ar@gmail.com");
        TicketRequest ticketRequest = new TicketRequest("london", "france", user, 5.0);
        Train train = new Train("Train1");
        Seat expectedSeat = new Seat("A1", true, user);
        Ticket expectedTicket = new Ticket("london", "france", 5.0, expectedSeat);

        when(trainRepo.get("Train1")).thenReturn(train);
        when(ticketRepo.save(anyString(), any(Ticket.class))).thenReturn(expectedTicket);

        // Execute the method under test
        Ticket resultTicket = ticketBookingService.purchaseTicket(ticketRequest);

        // Verify results
        assertNotNull(resultTicket);
        assertEquals(expectedTicket, resultTicket);
        assertEquals(ticketRequest.getFrom(), resultTicket.getFrom());
        assertEquals(ticketRequest.getTo(), resultTicket.getTo());
        assertEquals(ticketRequest.getPrice(), resultTicket.getPrice());
        assertTrue(resultTicket.getSeat().isOccupied());

        // Verify interactions
        verify(trainRepo, times(1)).get("Train1");
        verify(ticketRepo, times(1)).save(anyString(), any());
    }

    @Test
    void purchaseTicket_NoSeatsThrowsException() {
        TicketRequest request = new TicketRequest("From", "To", new User("John", "Doe", "john@example.com"), 100);
        Train train = new Train("Train");
        when(trainRepo.get("Train")).thenReturn(train);

        assertThrows(SeatNotAvailableException.class, () -> ticketBookingService.purchaseTicket(request));
    }

    @Test
    void modifySeat_Success() {
        String email = user.getEmail();
        Ticket existingTicket = new Ticket("london", "france", 5.0, new Seat("A1", true, user));

        Train train = new Train("Train1");
        when(trainRepo.get("Train1")).thenReturn(train);
        when(ticketRepo.findByEmail(email)).thenReturn(existingTicket);

        Ticket updatedTicket = ticketBookingService.modifySeat(email, "A2");

        // Capture the ticket argument when save is called
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepo).save(eq(email), ticketCaptor.capture());

        Ticket savedTicket = ticketCaptor.getValue();

        assertNotNull(savedTicket);
        assertEquals(5.0, savedTicket.getPrice());
        assertEquals("A2", savedTicket.getSeat().getNumber());
        assertTrue(savedTicket.getSeat().isOccupied()); // A2 should now be marked as occupied
        verify(ticketRepo, times(1)).save(anyString(), any());
    }

    @Test
    void modifySeat_TicketNotFound_ThrowsException() {
        String email = "nonexistent@example.com";
        when(ticketRepo.findByEmail(email)).thenReturn(null);

        assertThrows(TicketNotFoundException.class, () -> ticketBookingService.modifySeat(email, "A2"));
    }

    @Test
    void modifySeat_SeatNotAvailable_ThrowsException() {
        String email = user.getEmail();
        Ticket existingTicket = new Ticket();
        existingTicket.setSeat(new Seat("A1", true, user));

        Map<String, Section> sectionMap = Map.of("A", new Section("A", 50));
        sectionMap.get("A").getSeats().stream().filter(seat -> seat.getNumber().equals("A2")).findFirst().ifPresent(s -> s.setOccupied(true));
        Train train = new Train("Train1");
        train.setSections(sectionMap);
        when(trainRepo.get("Train1")).thenReturn(train);
        when(ticketRepo.findByEmail(email)).thenReturn(existingTicket);

        assertThrows(SeatNotAvailableException.class, () -> ticketBookingService.modifySeat(email, "A2"));
    }

    @Test
    public void removeUser_RemovesTicketAndFreesSeat_WhenUserExists() {
        String email = user.getEmail();
        Train train = new Train("Train1");
        Ticket existingTicket = new Ticket("london", "france", 5.0, new Seat("A1", true, user));

        when(ticketRepo.findByEmail(email)).thenReturn(existingTicket);
        when(trainRepo.get("Train1")).thenReturn(train);

        ticketBookingService.removeUser(email);
        verify(ticketRepo, times(1)).removeTicket(email);
    }

    @Test
    public void removeUser_InvalidSeat() {
        String email = user.getEmail();
        Train train = new Train("Train1");
        Ticket existingTicket = new Ticket("london", "france", 5.0, new Seat("Z1", true, user));

        when(ticketRepo.findByEmail(email)).thenReturn(existingTicket);
        when(trainRepo.get("Train1")).thenReturn(train);

        ticketBookingService.removeUser(email);
        verify(ticketRepo, times(1)).removeTicket(email);
    }

    @Test
    public void removeUser_ThrowsTicketNotFoundException_WhenTicketNotFound() {
        String email = "nonexistent@example.com";
        when(ticketRepo.findByEmail(email)).thenReturn(null);

        assertThrows(TicketNotFoundException.class, () -> ticketBookingService.removeUser(email));

        verify(ticketRepo, never()).removeTicket(email);
    }

    @Test
    public void getReceipt_ReturnsTicket_WhenEmailExists() {
        String email = user.getEmail();
        Ticket expectedTicket = new Ticket("london", "france", 5.0, new Seat("Z1", true, user));
        when(ticketRepo.findByEmail(email)).thenReturn(expectedTicket);

        Ticket result = ticketBookingService.getReceipt(email);

        assertEquals(expectedTicket, result, "Expected ticket was not returned.");
    }

    @Test
    public void getReceipt_ThrowsTicketNotFoundException_WhenTicketNotFound() {
        String email = "doesnotexist@example.com";
        when(ticketRepo.findByEmail(email)).thenReturn(null);

        assertThrows(TicketNotFoundException.class, () -> ticketBookingService.getReceipt(email),
                "Expected TicketNotFoundException was not thrown.");
    }
}
