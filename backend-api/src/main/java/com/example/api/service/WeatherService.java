package com.example.api.service;

import com.example.api.model.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast";

    public List<WeatherData> fetchWeatherData(String lat, String lon) throws Exception {
        List<WeatherData> weatherDataList = new ArrayList<>();
        String url = String.format("%s?latitude=%s&longitude=%s&daily=temperature_2m_max,temperature_2m_min&timezone=auto", 
                                 API_URL, lat, lon);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonResponse);

                JsonNode daily = root.path("daily");
                JsonNode dates = daily.path("time");
                JsonNode maxTemps = daily.path("temperature_2m_max");
                JsonNode minTemps = daily.path("temperature_2m_min");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Kolkata"));
                String fetchTime = Instant.now().atZone(ZoneId.of("Asia/Kolkata")).format(formatter);

                for (int i = 0; i < dates.size(); i++) {
                    WeatherData data = new WeatherData(
                            fetchTime,
                            lat,
                            lon,
                            dates.get(i).asText(),
                            maxTemps.get(i).asDouble(),
                            minTemps.get(i).asDouble()
                    );
                    weatherDataList.add(data);
                }
            }
        }
        return weatherDataList;
    }
}