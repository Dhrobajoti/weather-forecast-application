package com.example.csv.service;

import com.example.csv.model.WeatherData;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CsvService {
    private static final String CSV_FILE = "weather_data.csv";

    public CsvService() {
        initializeCsvFile();
    }

    private void initializeCsvFile() {
        if (!Files.exists(Paths.get(CSV_FILE))) {
            try (FileWriter writer = new FileWriter(CSV_FILE)) {
                writer.append("Fetch Time,Latitude,Longitude,Date,Max Temp (°C),Min Temp (°C)\n");
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialize CSV file", e);
            }
        }
    }

    public void saveToCsv(List<WeatherData> dataList) {
        try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
            for (WeatherData data : dataList) {
                writer.append(String.join(",",
                        data.getFetchTime(),
                        data.getLatitude(),
                        data.getLongitude(),
                        data.getDate(),
                        String.valueOf(data.getMaxTemp()),
                        String.valueOf(data.getMinTemp())
                ));
                writer.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save CSV data", e);
        }
    }
}