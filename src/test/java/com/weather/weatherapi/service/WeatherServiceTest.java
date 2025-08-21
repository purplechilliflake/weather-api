package com.weather.weatherapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.weatherapi.entity.PincodeLocation;
import com.weather.weatherapi.entity.Weather;
import com.weather.weatherapi.repository.PincodeLocationRepository;
import com.weather.weatherapi.repository.WeatherRepository;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private WeatherRepository weatherRepository;
    @Mock
    private PincodeLocationRepository pincodeLocationRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private WeatherService weatherService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-api-key");
    }

    @Test
    void testGetWeather_ReturnsCachedData_WhenAvailable() {
        // Arrange
        String pincode = "411014";
        LocalDate date = LocalDate.now();
        String cachedWeatherInfo = "{\"temp\": 25.0}";
        Weather cachedWeather = new Weather();
        cachedWeather.setWeatherInfo(cachedWeatherInfo);

        when(weatherRepository.findByPincodeAndDate(pincode, date)).thenReturn(Optional.of(cachedWeather));

        // Act
        String result = weatherService.getWeatherForPincodeAndDate(pincode, date);

        // Assert
        assertEquals(cachedWeatherInfo, result);
        verify(restTemplate, never()).getForObject(anyString(), any(Class.class), any(), any());
    }

    @Test
    void testGetWeather_FetchesFromApi_WhenNotInCache() throws JsonProcessingException {
        // Arrange
        String pincode = "411014";
        LocalDate date = LocalDate.now();
        double lat = 18.52;
        double lon = 73.85;
        String weatherApiResponse = "{\"weather\":[{\"main\":\"Haze\"}]}";

        PincodeLocation location = new PincodeLocation();
        location.setLatitude(lat);
        location.setLongitude(lon);

        String geocodingResponseStr = "{\"zip\":\"411014\", \"name\":\"Pune\", \"lat\":" + lat + ", \"lon\":" + lon
                + ", \"country\":\"IN\"}";
        JsonNode geocodingResponseJson = objectMapper.readTree(geocodingResponseStr);

        when(weatherRepository.findByPincodeAndDate(pincode, date)).thenReturn(Optional.empty());
        when(pincodeLocationRepository.findByPincode(pincode)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class), eq(pincode), anyString()))
                .thenReturn(geocodingResponseJson);
        when(pincodeLocationRepository.save(any(PincodeLocation.class))).thenReturn(location);
        when(restTemplate.getForObject(anyString(), eq(String.class), eq(lat), eq(lon), anyString()))
                .thenReturn(weatherApiResponse);

        // Act
        String result = weatherService.getWeatherForPincodeAndDate(pincode, date);

        // Assert
        assertEquals(weatherApiResponse, result);
        verify(weatherRepository, times(1)).save(any(Weather.class));
    }
}