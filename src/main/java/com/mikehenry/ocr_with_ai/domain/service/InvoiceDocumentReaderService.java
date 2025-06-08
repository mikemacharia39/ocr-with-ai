package com.mikehenry.ocr_with_ai.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikehenry.ocr_with_ai.api.dto.InvoiceDto;
import com.mikehenry.ocr_with_ai.domain.service.aimodel.OllamaService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class InvoiceDocumentReaderService {

    private final ChatClient chatClient;
    private final OllamaService ollamaService;

    private final ObjectMapper objectMapper;

    public InvoiceDto extractInvoiceDataFromImage(MultipartFile invoiceImageFile) {
        String prompt = """
            Read this invoice image and extract the invoice items. Return the data in the provided format.  
            If invoice details can be deciphered partially, the return only what could be read. At times the fields
            may not be a one to one match with the fields in the expected format, try as much as possible to figure
            out how the contents should be populated. If the invoice image cannot be deciphered, 
            then return an empty object.
            """;
        try {
            var inputStreamResource = new InputStreamResource(invoiceImageFile.getInputStream());

            InvoiceDto response = chatClient.prompt()
                    //.user(prompt + " Image: " + Arrays.toString(invoiceImageFile.getBytes()))
                    .user(userMessage -> userMessage
                            .text(prompt)
                            .media(
                                    MimeTypeUtils.parseMimeType(Objects.requireNonNull(invoiceImageFile.getContentType())),
                                    new InputStreamResource(inputStreamResource)
                            ))
                    .call()
                    .entity(InvoiceDto.class);
            System.out.println("Extracted Invoice Data: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("Error processing invoice image: " + e.getMessage());
            return new InvoiceDto("", "", null, null, null, null); // Return empty InvoiceDto on error
        }
    }

    public String extractInvoiceWithSemiStructuredResponse(MultipartFile file) {
        String promptText = """
                Extract data from the invoice image. Return a valid JSON for the extracted data.
                Return in this format
                {"invoiceNumber":"[Invoice Number]","invoiceDate":"[Order Date or Invoice Date]",
                "items":[{"description":"[Item Description or Service Description]",
                "quantity":"[Quantity]","unitPrice":"[Unit Price]","totalPrice":"[Total price of the item]"}],
                "subTotalAmount":"[Sub Total Amount]","discount":"[Discount Amount]","totalAmount":"[Total Amount]"}
                """;
        try {
            var inputStreamResource = new InputStreamResource(file.getInputStream());
            String response = chatClient.prompt()
                    .user(userMessage -> userMessage
                            .text(promptText)
                            .media(
                                    MimeTypeUtils.parseMimeType(Objects.requireNonNull(file.getContentType())),
                                    new InputStreamResource(inputStreamResource)
                            ))
                    .call()
                    .content();
            System.out.println("Extracted Invoice Summary: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("Error processing invoice image: " + e.getMessage());
            return ""; // Return empty string on error
        }
    }

    public String extractDataFromInvoiceViaOllama(MultipartFile invoiceImageFile) {
        return ollamaService.getDataFromImage(invoiceImageFile);
    }

    private String promptFromSummaryData() {
        return """
                Given the provided invoice image, extract all information. Respond using JSON in the structure provided in the instructions.
                
                **Instructions:**
                1. **The items/service/prescriptions will be listed in a tabular format, with each item having the following fields:**
                
                    - **Description:** A brief description of the item or service.
                    - **Quantity:** The number of units provided.
                    - **Unit Price:** The price per unit of the item or service.
                    - **Total Price:** The total cost for the item or service (calculated as Quantity * Unit Price).
                
                2. **The columns may not necessarily be in the above order or name as provided above, so try to figure out how the contents should be populated.**
                
                3.  **JSON Output Format:**  Use the following JSON structure to present the extracted information:
                
                    ```json
                      {
                        "invoiceNumber": "[Invoice Number]",
                        "invoiceDate": "[Order Date or Invoice Date]"
                        "items": [
                          {
                            "description": "[Item Description or Service Description]",
                            "quantity": [Quantity],
                            "unitPrice": [Unit Price],
                            "totalPrice": [Total price of the item]
                          },
                          // ... (Repeat for all items) ...
                        ],
                        "subTotalAmount": "[Sub Total Amount]",
                        "discount": "[Discount Amount]",
                        "totalAmount": "[Total Amount]"
                      }
                    ```
                """;
    }
}
