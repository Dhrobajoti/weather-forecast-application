package com.example.csv.controller;

import com.example.csv.model.WeatherData;
import com.example.csv.service.CsvService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/csv")
public class CsvController {
    private final CsvService csvService;

    public CsvController(CsvService csvService) {
        this.csvService = csvService;
    }

    @PostMapping("/save")
    public String saveToCsv(@RequestBody List<WeatherData> weatherData) {
        csvService.saveToCsv(weatherData);
        return "Data saved to CSV successfully!";
    }
}