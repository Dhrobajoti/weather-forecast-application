package com.example.csv.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherData {
    @JsonProperty("fetchTime")
    private String fetchTime;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("longitude")
    private String longitude;
    @JsonProperty("date")
    private String date;
    @JsonProperty("maxTemp")
    private double maxTemp;
    @JsonProperty("minTemp")
    private double minTemp;

    // Default constructor
    public WeatherData() {}

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