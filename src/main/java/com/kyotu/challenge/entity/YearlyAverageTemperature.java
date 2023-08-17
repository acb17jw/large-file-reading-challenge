package com.kyotu.challenge.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class YearlyAverageTemperature {
    private String year;
    private double averageTemperature;
}
