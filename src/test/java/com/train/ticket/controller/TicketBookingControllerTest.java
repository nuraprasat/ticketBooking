package com.train.ticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.train.ticket.model.TicketRequest;
import com.train.ticket.model.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TicketBookingControllerTest {
    private static final String ticketBooking = "/v1/tickets/purchase";
    private static final String getReceipt = "/v1/tickets/receipt/%s";
    private static final String deleteBookings = "/v1/tickets/remove/%s";
    private static final String getUserAndSeat = "/v1/tickets/users";
    @Autowired
    private MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testPurchaseTicket_success() throws Exception {
        String deleteBook = String.format(deleteBookings, "ar@gmail.com");
        this.mockMvc.perform(MockMvcRequestBuilders.delete(deleteBook).contentType(MediaType.APPLICATION_JSON));

        User user = new User("aru", "pr", "ar@gmail.com");
        TicketRequest ticketRequest = new TicketRequest("eur", "lon", user, 11.0);
        this.mockMvc.perform(MockMvcRequestBuilders.post(ticketBooking).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.from", Matchers.equalTo("eur")))
                .andExpect(jsonPath("$.to", Matchers.equalTo("lon")))
                .andExpect(jsonPath("$.seat.user.email", Matchers.equalTo("ar@gmail.com")))
                .andExpect(jsonPath("$.seat.number", Matchers.equalTo("A1")));
    }

    @Test
    void testGetReceipt_throwsException() throws Exception {
        String uri = String.format(getReceipt, "ar@gmail.com");
        this.mockMvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.equalTo("Ticket not found")));
    }

    @Test
    void testGetReceipt_success() throws Exception {
        User user = new User("aru", "pr", "ar@gmail.com");
        TicketRequest ticketRequest = new TicketRequest("eur", "lon", user, 11.0);
        this.mockMvc.perform(MockMvcRequestBuilders.post(ticketBooking).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketRequest)));

        String uri = String.format(getReceipt, "ar@gmail.com");
        this.mockMvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from", Matchers.equalTo("eur")))
                .andExpect(jsonPath("$.to", Matchers.equalTo("lon")))
                .andExpect(jsonPath("$.seat.user.email", Matchers.equalTo("ar@gmail.com")))
                .andExpect(jsonPath("$.seat.number", Matchers.equalTo("A1")));
    }
}
