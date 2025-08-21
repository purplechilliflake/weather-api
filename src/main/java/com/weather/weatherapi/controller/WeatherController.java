package com.weather.weatherapi.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weather.weatherapi.service.WeatherService;

@RestController
@RequestMapping("api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public ResponseEntity<String> getWeather(
            @RequestParam("pincode") String pincode,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            String weatherData = weatherService.getWeatherForPincodeAndDate(pincode, date);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            e.printStackTrace(); // For debugging on the server console
            return ResponseEntity.status(500).body("Error fetching weather data: " + e.getMessage());
        }
    }
}
