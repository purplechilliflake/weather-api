package com.weather.weatherapi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.weather.weatherapi.entity.PincodeLocation;

public interface PincodeLocationRepository extends JpaRepository<PincodeLocation, Long> {

    Optional<PincodeLocation> findByPincode(String pincode);
}
