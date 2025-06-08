package com.mikehenry.ocr_with_ai.domain.client;

import com.mikehenry.ocr_with_ai.api.dto.OllamaVisionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ollama-client", url = "${ollama.api.url}")
public interface OllamaClient {

    @PostMapping("/api/chat")
    String chat(@RequestBody OllamaVisionRequest ollamaVisionRequest);
}
