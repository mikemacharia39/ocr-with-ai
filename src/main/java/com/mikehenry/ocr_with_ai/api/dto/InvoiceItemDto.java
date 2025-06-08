package com.mikehenry.ocr_with_ai.api.dto;

import java.math.BigDecimal;

public record InvoiceItemDto(
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}
