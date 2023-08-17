package com.kyotu.challenge.controller;

import com.kyotu.challenge.entity.YearlyAverageTemperature;
import com.kyotu.challenge.service.TemperatureStatisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(TemperatureStatisticsController.class)
public class TemperatureStatisticsControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TemperatureStatisticsService temperatureStatisticsService;

    @Test
    public void testGetYearlyAverages() throws Exception {
        List<YearlyAverageTemperature> mockAverages = new ArrayList<>();
        mockAverages.add(new YearlyAverageTemperature("2021", 12.5));

        when(temperatureStatisticsService.readTemperatureDataFromCSV(anyString())).thenReturn(new ArrayList<>());
        when(temperatureStatisticsService.calculateYearlyAverages(anyList())).thenReturn(mockAverages);

        mockMvc.perform(get("/api/temperatures/Warszawa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].year").value("2021"))
                .andExpect(jsonPath("$[0].averageTemperature").value(12.5));
    }
}
