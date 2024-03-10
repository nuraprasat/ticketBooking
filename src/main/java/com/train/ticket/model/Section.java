package com.train.ticket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class Section {
    private String name;
    private List<Seat> seats;

    public Section(String name, int capacity) {
        this.name = name;
        this.seats = new ArrayList<>();
        for (int i = 1; i <= capacity; i++) {
            seats.add(new Seat(name + i, false, null));
        }
    }

    public Optional<Seat> getNextAvailableSeat() {
        return seats.stream().filter(seat -> !seat.isOccupied()).findFirst();
    }

    public List<Seat> getOccupiedSeats() {
        return seats.stream().filter(Seat::isOccupied).collect(Collectors.toList());
    }
}
