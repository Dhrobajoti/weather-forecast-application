package com.example.api.controller;

import com.example.api.model.WeatherData;
import com.example.api.service.WeatherService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherApiController {
    private final WeatherService weatherService;

    public WeatherApiController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = "weatherCache", key = "{#lat, #lon}")
    public List<WeatherData> getWeatherData(
            @RequestParam String lat,
            @RequestParam String lon) throws Exception {
        return weatherService.fetchWeatherData(lat, lon);
    }
}