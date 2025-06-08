package com.mikehenry.ocr_with_ai.domain.service.aimodel;

import com.mikehenry.ocr_with_ai.api.dto.OllamaVisionRequest;
import com.mikehenry.ocr_with_ai.domain.client.OllamaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OllamaService {

    private final OllamaClient ollamaClient;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String aiModel;

    public String getDataFromImage(MultipartFile imageFile) {

        final OllamaVisionRequest visionRequest = OllamaVisionRequest.builder()
                .model(aiModel)
                .stream(false)
                .messages(List.of(OllamaVisionRequest.OllamaMessage.builder()
                                .role("user")
                                .content(promptMessage())
                                .images(List.of(encodeImageToBase64(imageFile)))
                        .build()))
                .build();

        final String response = ollamaClient.chat(visionRequest);

        log.info("Ollama response: {}", response);

        return response;
    }

    private String encodeImageToBase64(MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            return java.util.Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode image to Base64", e);
        }
    }

    private String promptMessage() {
        return "Extract data from the invoice image. Return a valid JSON for the extracted data. Return in this format {\"invoiceNumber\":\"[Invoice Number]\",\"invoiceDate\":\"[Order Date or Invoice Date]\",\"items\":[{\"description\":\"[Item Description or Service Description]\",\"quantity\":\"[Quantity]\",\"unitPrice\":\"[Unit Price]\",\"totalPrice\":\"[Total price of the item]\"}],\"subTotalAmount\":\"[Sub Total Amount]\",\"discount\":\"[Discount Amount]\",\"totalAmount\":\"[Total Amount]\"}";
    }
}
