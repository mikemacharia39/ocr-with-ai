package com.mikehenry.ocr_with_ai.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record OllamaVisionRequest(
        String model,
        boolean stream,
        List<OllamaMessage> messages
) {

    @Builder
    public record OllamaMessage(
            String role,
            String content,
            List<String> images
    ) {
    }
}
