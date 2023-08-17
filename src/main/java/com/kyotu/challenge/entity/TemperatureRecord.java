package com.kyotu.challenge.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class TemperatureRecord {
    private String city;
    private LocalDateTime timestamp;
    private double temperature;
}
