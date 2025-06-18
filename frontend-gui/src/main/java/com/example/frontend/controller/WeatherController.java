package com.example.frontend.controller;

import com.example.frontend.model.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class WeatherController {
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${backend.api.url}")
    private String apiServiceUrl;

    @Value("${backend.graph.url}")
    private String graphServiceUrl;

    @Value("${backend.csv.url}")
    private String csvServiceUrl;

    @Value("${backend.db.url}")
    private String dbServiceUrl;

    @Value("${backend.exporter.url}")
    private String exporterServiceUrl;

    public WeatherController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("hasData", false);
        return "index";
    }

    @PostMapping("/")
    public String getForecast(
            @RequestParam String latitude,
            @RequestParam String longitude,
            Model model) {
        
        if (!isValidCoordinate(latitude) || !isValidCoordinate(longitude)) {
            model.addAttribute("error", "Invalid coordinates format");
            model.addAttribute("hasData", false);
            return "index";
        }

        try {
            WeatherData[] weatherDataArray = webClient.get()
                .uri(apiServiceUrl + "/api/weather?lat={lat}&lon={lon}", latitude, longitude)
                .retrieve()
                .bodyToMono(WeatherData[].class)
                .block();

            List<WeatherData> weatherData = Arrays.asList(weatherDataArray);
            model.addAttribute("data", weatherData);
            model.addAttribute("hasData", !weatherData.isEmpty());
            model.addAttribute("fetchTime", weatherData.isEmpty() ? "" : weatherData.get(0).getFetchTime());
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching weather data: " + e.getMessage());
            model.addAttribute("hasData", false);
        }
        return "index";
    }

    @PostMapping("/save-csv")
    public String saveToCsv(@RequestParam("data") String[] dataArray, Model model) {
        try {
            List<WeatherData> data = Arrays.stream(dataArray)
                .map(item -> {
                    try {
                        String jsonStr = "{" + 
                            Arrays.stream(item.split(","))
                                .map(pair -> {
                                    String[] kv = pair.split("=", 2);
                                    return "\"" + kv[0] + "\":\"" + (kv.length > 1 ? kv[1] : "") + "\"";
                                })
                                .collect(Collectors.joining(",")) + 
                            "}";
                        return objectMapper.readValue(jsonStr, WeatherData.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse weather data: " + item, e);
                    }
                })
                .collect(Collectors.toList());
    
            String jsonData = objectMapper.writeValueAsString(data);
    
            webClient.post()
                .uri(csvServiceUrl + "/api/csv/save")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonData)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    
            model.addAttribute("message", "Data saved to CSV successfully!");
            model.addAttribute("hasData", true);
            model.addAttribute("data", data);
            if (!data.isEmpty()) {
                model.addAttribute("fetchTime", data.get(0).getFetchTime());
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", "Failed to process data: " + e.getCause().getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to save CSV: " + e.getMessage());
        }
        return "index";
    }

    @PostMapping("/plot-graph")
    public String plotGraph(
        @RequestParam("data") String[] dataArray,
        Model model) {
        
        try {
            List<WeatherData> data = new ArrayList<>();
            for (String item : dataArray) {
                try {
                    String jsonStr = "{" + 
                        item.replaceAll("([^,=]+)=([^,]*)", "\"$1\":\"$2\"") + 
                        "}";
                    WeatherData weatherData = objectMapper.readValue(jsonStr, WeatherData.class);
                    data.add(weatherData);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Failed to parse weather data: " + item, e);
                }
            }

            String plotResponse;
            try {
                plotResponse = webClient.post()
                    .uri(graphServiceUrl + "/api/graph/plot")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(data))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            } catch (WebClientResponseException e) {
                throw new RuntimeException("Graph service returned error: " + e.getResponseBodyAsString(), e);
            }

            JsonNode plotJson;
            try {
                plotJson = objectMapper.readTree(plotResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Invalid response from graph service", e);
            }

            if (!plotJson.has("plot")) {
                throw new RuntimeException("Missing 'plot' field in response");
            }
            String base64Image = plotJson.get("plot").asText();
            if (base64Image == null || base64Image.isEmpty()) {
                throw new RuntimeException("Empty image data received");
            }

            model.addAttribute("plotData", base64Image);
            model.addAttribute("hasData", true);
            model.addAttribute("data", data);
            if (!data.isEmpty()) {
                model.addAttribute("fetchTime", data.get(0).getFetchTime());
            }

        } catch (RuntimeException e) {
            model.addAttribute("error", "Graph error: " + 
                (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        } catch (Exception e) {
            model.addAttribute("error", "Unexpected error: " + e.getMessage());
        }
        return "index";
    }

    @PostMapping("/save-db")
    public String saveToDb(@RequestParam("data") String[] dataArray, Model model) {
        try {
            List<WeatherData> data = Arrays.stream(dataArray)
                .map(item -> {
                    try {
                        String jsonStr = "{" + 
                            Arrays.stream(item.split(","))
                                .map(pair -> {
                                    String[] kv = pair.split("=", 2);
                                    return "\"" + kv[0] + "\":\"" + (kv.length > 1 ? kv[1] : "") + "\"";
                                })
                                .collect(Collectors.joining(",")) + 
                            "}";
                        return objectMapper.readValue(jsonStr, WeatherData.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse weather data: " + item, e);
                    }
                })
                .collect(Collectors.toList());

            String jsonData = objectMapper.writeValueAsString(data);

            webClient.post()
                .uri(dbServiceUrl + "/api/db/save")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonData)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            model.addAttribute("message", "Data saved to database successfully!");
            model.addAttribute("hasData", true);
            model.addAttribute("data", data);
            if (!data.isEmpty()) {
                model.addAttribute("fetchTime", data.get(0).getFetchTime());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to save to database: " + e.getMessage());
        }
        return "index";
    }

    @PostMapping("/export-csv")
    public ResponseEntity<byte[]> exportToCsv(@RequestParam("data") String[] dataArray) {
        try {
            List<WeatherData> data = convertDataArrayToList(dataArray);
            byte[] csvFile = webClient.post()
                .uri(exporterServiceUrl + "/api/export/csv")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(data))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=weather_data.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvFile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CSV export failed", e);
        }
    }

    @PostMapping("/export-excel")
    public ResponseEntity<byte[]> exportToExcel(@RequestParam("data") String[] dataArray) {
        try {
            List<WeatherData> data = convertDataArrayToList(dataArray);
            byte[] excelFile = webClient.post()
                .uri(exporterServiceUrl + "/api/export/excel")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(data))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=weather_data.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelFile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Excel export failed", e);
        }
    }

    private List<WeatherData> convertDataArrayToList(String[] dataArray) throws JsonProcessingException {
        return Arrays.stream(dataArray)
            .map(item -> {
                try {
                    String jsonStr = "{" + 
                        Arrays.stream(item.split(","))
                            .map(pair -> {
                                String[] kv = pair.split("=", 2);
                                return "\"" + kv[0] + "\":\"" + (kv.length > 1 ? kv[1] : "") + "\"";
                            })
                            .collect(Collectors.joining(",")) + 
                        "}";
                    return objectMapper.readValue(jsonStr, WeatherData.class);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse weather data", e);
                }
            })
            .collect(Collectors.toList());
    }

    private boolean isValidCoordinate(String coord) {
        try {
            double value = Double.parseDouble(coord);
            return value >= -180 && value <= 180;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}