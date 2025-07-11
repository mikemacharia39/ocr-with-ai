package com.mikehenry.ocr_with_ai.domain.repository;

import com.mikehenry.ocr_with_ai.domain.entity.Temperature;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemperatureRepository extends ListCrudRepository<Temperature, Long> {
    @Query("""
        SELECT id, senssor_ref as sensorRef, location, temp, recorded_at as recordedAt FROM temperature
    """)
    List<Temperature> findAllTemperatures();
}
