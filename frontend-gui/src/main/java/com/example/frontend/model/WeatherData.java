package com.example.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    private String fetchTime;
    private String latitude;
    private String longitude;
    private String date;
    private double maxTemp;
    private double minTemp;

    public WeatherData() {}

    public WeatherData(String fetchTime, String latitude, String longitude, 
                      String date, double maxTemp, double minTemp) {
        this.fetchTime = fetchTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
    }

    // Getters and setters
    public String getFetchTime() { return fetchTime; }
    public void setFetchTime(String fetchTime) { this.fetchTime = fetchTime; }
    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public double getMaxTemp() { return maxTemp; }
    public void setMaxTemp(double maxTemp) { this.maxTemp = maxTemp; }
    public double getMinTemp() { return minTemp; }
    public void setMinTemp(double minTemp) { this.minTemp = minTemp; }
}