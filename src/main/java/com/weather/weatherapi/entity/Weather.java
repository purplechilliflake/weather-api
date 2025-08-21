package com.weather.weatherapi.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "weather_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "pincode", "date" })
})
@Data
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pincode;

    @Column(nullable = false)
    private LocalDate date;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String weatherInfo;
}
