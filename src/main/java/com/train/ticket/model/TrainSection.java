package com.train.ticket.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TrainSection {
    A,
    B;

    @JsonCreator
    public static TrainSection forValue(String key) {
        try {
            return TrainSection.valueOf(key.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
