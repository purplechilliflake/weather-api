package com.weather.weatherapi.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.weather.weatherapi.entity.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Optional<Weather> findByPincodeAndDate(String pincode, LocalDate date);
}
