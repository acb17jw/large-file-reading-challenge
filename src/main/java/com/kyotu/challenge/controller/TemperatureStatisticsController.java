package com.kyotu.challenge.controller;

import com.kyotu.challenge.entity.TemperatureRecord;
import com.kyotu.challenge.entity.YearlyAverageTemperature;
import com.kyotu.challenge.service.TemperatureStatisticsService;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/temperatures")
public class TemperatureStatisticsController {

    private static final Logger LOG = Logger.getLogger(TemperatureStatisticsController.class.getName());

    private final TemperatureStatisticsService temperatureStatisticsService;

    @Value("${csv.file.path}")
    private String filePath;


    @Autowired
    public TemperatureStatisticsController(TemperatureStatisticsService temperatureStatisticsService) {
        this.temperatureStatisticsService = temperatureStatisticsService;
    }

    @GetMapping("/{city}")
    public List<YearlyAverageTemperature> getYearlyAverages(@PathVariable String city) {
        try {
            List<TemperatureRecord> temperatureRecordList = temperatureStatisticsService.readTemperatureDataFromCSV(filePath);

            List<TemperatureRecord> cityData = temperatureRecordList.stream()
                    .filter(data -> data.getCity().equalsIgnoreCase(city))
                    .collect(Collectors.toList());

            return temperatureStatisticsService.calculateYearlyAverages(cityData);
        } catch (IOException | CsvValidationException e) {
            LOG.warning("There was an issue with reading data from csv file. Returning empty list.");
            return Collections.emptyList();
        }
    }
}
