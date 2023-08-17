package com.kyotu.challenge.service;

import com.kyotu.challenge.entity.TemperatureRecord;
import com.kyotu.challenge.entity.YearlyAverageTemperature;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TemperatureStatisticsService {

    private final ResourceLoader resourceLoader;

    public TemperatureStatisticsService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<YearlyAverageTemperature> calculateYearlyAverages(List<TemperatureRecord> temperatureRecordList) {
        Map<Integer, List<TemperatureRecord>> dataByYear = temperatureRecordList.stream()
                .collect(Collectors.groupingBy(data -> data.getTimestamp().getYear()));

        List<YearlyAverageTemperature> yearlyAverages = new ArrayList<>();

        for (Map.Entry<Integer, List<TemperatureRecord>> entry : dataByYear.entrySet()) {
            int year = entry.getKey();
            List<TemperatureRecord> yearData = entry.getValue();
            double averageTemperature = yearData.stream()
                    .mapToDouble(TemperatureRecord::getTemperature)
                    .average()
                    .orElse(0);

            yearlyAverages.add(new YearlyAverageTemperature(String.valueOf(year), averageTemperature));
        }

        return yearlyAverages;
    }

    public List<TemperatureRecord> readTemperatureDataFromCSV(String filePath) throws IOException, CsvValidationException {
        List<TemperatureRecord> temperatureRecordList = new ArrayList<>();
        Resource resource = resourceLoader.getResource(filePath);
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

        try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(resource.getInputStream()))
                .withCSVParser(parser)
                .build()) {
            String[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null) {
                TemperatureRecord temperatureRecord = TemperatureRecord.builder()
                        .city(nextRecord[0])
                        .timestamp(LocalDateTime.parse(nextRecord[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")))
                        .temperature(Double.parseDouble(nextRecord[2]))
                        .build();

                temperatureRecordList.add(temperatureRecord);
            }
        }

        return temperatureRecordList;
    }
}
