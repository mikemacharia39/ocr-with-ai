package com.mikehenry.ocr_with_ai.domain.entity;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.Instant;

public record Temperature(
        @Id
        long id,
        String sensorRef,
        String location,
        BigDecimal temp,
        Instant recordedAt
) {
}
