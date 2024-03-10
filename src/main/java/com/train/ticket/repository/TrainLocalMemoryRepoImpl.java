package com.train.ticket.repository;

import com.train.ticket.model.Seat;
import com.train.ticket.model.Section;
import com.train.ticket.model.Ticket;
import com.train.ticket.model.Train;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TrainLocalMemoryRepoImpl implements TrainRepo {
    private final Map<String, Train> trains = new ConcurrentHashMap<>();

    public TrainLocalMemoryRepoImpl() {
        trains.put("Train1", new Train("Train1"));
    }

    public Train get(String trainId) {
        return this.trains.get(trainId);
    }

    @Override
    public void save(String trainId, Train train) {
        this.trains.put(trainId, train);
    }

    public List<Seat> getSeatBySection(String section) {
        return this.trains.get("Train1").getSections().get(section).getOccupiedSeats();
    }
}
