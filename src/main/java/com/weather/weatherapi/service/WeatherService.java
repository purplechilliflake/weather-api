package com.weather.weatherapi.service;

import java.time.LocalDate;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.weather.weatherapi.entity.PincodeLocation;
import com.weather.weatherapi.entity.Weather;
import com.weather.weatherapi.repository.PincodeLocationRepository;
import com.weather.weatherapi.repository.WeatherRepository;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;
    @Autowired
    private PincodeLocationRepository pincodeLocationRepository;
    @Autowired
    RestTemplate restTemplate;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private static final String GEOCODING_API_URL = "https://api.openweathermap.org/geo/1.0/zip?zip={pincode},IN&appid={apiKey}";
    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={apiKey}";

    public String getWeatherForPincodeAndDate(String pincode, LocalDate date) {
        Optional<Weather> cachedWeather = weatherRepository.findByPincodeAndDate(pincode, date);
        if (cachedWeather.isPresent()) {
            logger.info("Returning cached weather data for pincode: {}", pincode);
            return cachedWeather.get().getWeatherInfo();
        }

        logger.info("No cached weather data for pincode: {}. Fetching from API...", pincode);
        PincodeLocation location = getCoordinatesForPincode(pincode);

        String weatherInfoJson = fetchWeatherFromApi(location.getLatitude(), location.getLongitude());

        Weather newWeather = new Weather();
        newWeather.setPincode(pincode);
        newWeather.setDate(date);
        newWeather.setWeatherInfo(weatherInfoJson);
        weatherRepository.save(newWeather);
        logger.info("Saved new weather data for pincode: {}", pincode);

        return weatherInfoJson;
    }

    private PincodeLocation getCoordinatesForPincode(String pincode) {
        Optional<PincodeLocation> location = pincodeLocationRepository.findByPincode(pincode);
        if (location.isPresent()) {
            logger.info("Returning cached coordinates for pincode: {}", pincode);
            return location.get();
        }

        logger.info("No cached coordinates. Fetching from Geocoding API...");
        JsonNode response = restTemplate.getForObject(GEOCODING_API_URL, JsonNode.class, pincode, apiKey);

        double lat = response.get("lat").asDouble();
        double lon = response.get("lon").asDouble();

        PincodeLocation newLocation = new PincodeLocation();
        newLocation.setPincode(pincode);
        newLocation.setLatitude(lat);
        newLocation.setLongitude(lon);
        return pincodeLocationRepository.save(newLocation);
    }

    private String fetchWeatherFromApi(double lat, double lon) {
        return restTemplate.getForObject(WEATHER_API_URL, String.class, lat, lon, apiKey);
    }
}
