package com.mikehenry.ocr_with_ai.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record InvoiceDto(
    String  invoiceNumber,
    String  invoiceDate,
    BigDecimal subTotalAmount,
    BigDecimal discount,
    BigDecimal totalAmount,
    List<InvoiceItemDto> items
){
}