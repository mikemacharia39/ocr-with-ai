package com.mikehenry.ocr_with_ai.infra.configuration;

import com.mikehenry.ocr_with_ai.domain.repository.TemperatureRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
public class ChatClientConfig {
    @Bean
    ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }

    @Bean
    QuestionAnswerAdvisor questionAnswerAdvisor(VectorStore vectorStore) {
        return new QuestionAnswerAdvisor(vectorStore);
    }

    @Bean
    CommandLineRunner ingest(TemperatureRepository repo,
                             VectorStore store) {
        return args -> {
            for (var t : repo.findAll()) {
                String docText = String.format(
                        "Sensor %s recorded %s°C at %s in %s",
                        t.sensorRef(), t.temp(), t.recordedAt(), t.location()
                );


                // 2) metadata
                Map<String,Object> meta = Map.of(
                        "id", t.id(),
                        "sensorRef", t.sensorRef(),
                        "location", t.location()
                );

                // 3) build Document
                var doc = Document.builder()
                        .id(UUID.randomUUID().toString())
                        .metadata(meta)
                        .text(docText)
                        .build();

                // 4) add to vector store
                store.add(List.of(doc));
            }
        };
    }

}
