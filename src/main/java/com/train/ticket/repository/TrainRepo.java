package com.train.ticket.repository;

import com.train.ticket.model.Seat;
import com.train.ticket.model.Section;
import com.train.ticket.model.Train;

import java.util.List;

public interface TrainRepo {
    Train get(String trainId);
    void save(String trainId, Train train);
    List<Seat> getSeatBySection(String section);
}
