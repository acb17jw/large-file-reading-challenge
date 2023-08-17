package com.kyotu.challenge.service;

import com.kyotu.challenge.entity.TemperatureRecord;
import com.kyotu.challenge.entity.YearlyAverageTemperature;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemperatureStatisticsServiceTest {

    @InjectMocks
    private TemperatureStatisticsService temperatureStatisticsService;

    @Mock
    private ResourceLoader resourceLoader;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        temperatureStatisticsService = new TemperatureStatisticsService(resourceLoader);
    }

    @Test
    public void testCalculateYearlyAverages() {
        List<TemperatureRecord> testData = new ArrayList<>();
        testData.add(TemperatureRecord.builder()
                .city("Warszawa")
                .timestamp(LocalDateTime.of(2021, 1, 1, 0, 0))
                .temperature(15.0)
                .build());

        testData.add(TemperatureRecord.builder()
                .city("Warszawa")
                .timestamp(LocalDateTime.of(2021, 1, 1, 0, 0))
                .temperature(10.0)
                .build());

        List<YearlyAverageTemperature> averages = temperatureStatisticsService.calculateYearlyAverages(testData);

        assertEquals(1, averages.size());
        assertEquals("2021", averages.get(0).getYear());
        assertEquals(12.5, averages.get(0).getAverageTemperature(), 0.001);
    }

    @Test
    public void testReadTemperatureDataFromCSV() throws IOException, CsvValidationException {
        String csvContent = "Warszawa;2022-01-01 12:00:00.000;20.0\n"
                + "Warszawa;2022-01-02 12:00:00.000;25.0\n";

        Resource mockResource = new InputStreamResource(new ByteArrayInputStream(csvContent.getBytes()));

        when(resourceLoader.getResource(any())).thenReturn(mockResource);

        List<TemperatureRecord> temperatureRecords = temperatureStatisticsService.readTemperatureDataFromCSV("test.csv");

        assertEquals(2, temperatureRecords.size());
        assertEquals("Warszawa", temperatureRecords.get(0).getCity());
        assertEquals(LocalDateTime.parse("2022-01-01 12:00:00.000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), temperatureRecords.get(0).getTimestamp());
        assertEquals(20.0, temperatureRecords.get(0).getTemperature(), 0.001);
    }

    @Test
    public void testReadTemperatureDataFromEmptyCSV() throws IOException, CsvValidationException {
        String csvContent = "";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        Resource mockResource = new InputStreamResource(inputStream);

        when(resourceLoader.getResource(any())).thenReturn(mockResource);

        List<TemperatureRecord> temperatureRecords = temperatureStatisticsService.readTemperatureDataFromCSV("test.csv");

        assertEquals(0, temperatureRecords.size());
    }

    @Test
    public void testReadTemperatureDataFromInvalidCSV() {
        String csvContent = "Warszawa;2022-01-01 12:00:00.000";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        Resource mockResource = new InputStreamResource(inputStream);

        when(resourceLoader.getResource(any())).thenReturn(mockResource);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            temperatureStatisticsService.readTemperatureDataFromCSV("test.csv");
        });
    }
}
