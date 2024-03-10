package com.train.ticket.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Train {
    private String id; // Unique identifier for each train
    private Map<String, Section> sections;

    public Train(String id) {
        this.id = id;
        this.sections = new HashMap<>();
        // Assuming each section has 50 seats
        this.sections.put("A", new Section("A", 50));
        this.sections.put("B", new Section("B", 50));
    }
}
