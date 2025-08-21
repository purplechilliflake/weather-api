package com.weather.weatherapi.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.weather.weatherapi.service.WeatherService;

@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    void testGetWeather_ReturnsSuccess() throws Exception {
        // Arrange
        String pincode = "411014";
        LocalDate date = LocalDate.of(2024, 1, 1);
        String dateString = "2024-01-01";
        String weatherJson = "{\"temp\": 22.0}";

        when(weatherService.getWeatherForPincodeAndDate(pincode, date)).thenReturn(weatherJson);

        // Act & Assert
        mockMvc.perform(get("/api/weather")
                .param("pincode", pincode)
                .param("date", dateString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(weatherJson));
    }

    @Test
    void testGetWeather_ReturnsError_WhenServiceThrowsException() throws Exception {
        // Arrange
        String pincode = "411014";
        LocalDate date = LocalDate.of(2024, 1, 1);
        String dateString = "2024-01-01";

        when(weatherService.getWeatherForPincodeAndDate(pincode, date))
                .thenThrow(new RuntimeException("Service failure"));

        // Act & Assert
        mockMvc.perform(get("/api/weather")
                .param("pincode", pincode)
                .param("date", dateString))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}