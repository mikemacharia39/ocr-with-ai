package com.mikehenry.ocr_with_ai.api;

import com.mikehenry.ocr_with_ai.api.dto.InvoiceDto;
import com.mikehenry.ocr_with_ai.domain.service.ChatService;
import com.mikehenry.ocr_with_ai.domain.service.InvoiceDocumentReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequestMapping("/ai")
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;
    private final InvoiceDocumentReaderService invoiceReaderService;

    @GetMapping("/greetings")
    public String sampleChat() {
        return chatService.sampleChat("Hello, how are you?");
    }

    @PostMapping("/invoices/semi-structured")
    public String invoiceSummary(@RequestPart(name = "file") MultipartFile file) {
        return invoiceReaderService.extractInvoiceWithSemiStructuredResponse(file);
    }

    @PostMapping("/invoices/structured")
    public InvoiceDto structuredInvoiceData(@RequestPart(name = "file") MultipartFile file) {
        return invoiceReaderService.extractInvoiceDataFromImage(file);
    }

    @PostMapping("/invoices/unstructured-with-ollama")
    public String unstructuredInvoiceResponse(@RequestPart(name = "file") MultipartFile file) {
        return invoiceReaderService.extractDataFromInvoiceViaOllama(file);
    }
}
