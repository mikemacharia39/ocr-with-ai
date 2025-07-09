package com.mikehenry.ocr_with_ai.api.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/temperatures")
public class TemperatureController {

    private final ChatClient chatClient;
    private final QuestionAnswerAdvisor questionAnswerAdvisor;
    private final TemperatureRepository temperatureRepository;
    private final ChatMemoryRepository chatMemoryRepository = new InMemoryChatMemoryRepository();
    private static final String SYSTEM_PROMPT = """
            You are a helpful assistant that provides information about temperature readings.
            You can answer questions about the current temperature, historical data, and sensor locations.
            You can also provide insights on averages, trends, and anomalies in the temperature data if a user asks for it.
            If you don't know the answer, you return response that you do not have such data of the temperature readings.
           """;

    // to be able to remember previous conversations with a user, we need to momorize the chat history
    private final Map<String, PromptChatMemoryAdvisor> advisorMap = new ConcurrentHashMap<>();

    @GetMapping("/{user}/inquiries")
    public String inquireTemperatures(@PathVariable("user") String user, @RequestParam String question) {
        var advisor = advisorMap.computeIfAbsent( user,
                advisorMemory -> PromptChatMemoryAdvisor.builder(
                        MessageWindowChatMemory.builder()
                                .maxMessages(10)
                                .chatMemoryRepository(chatMemoryRepository)
                                .build())
                        .build());

        return chatClient
                .prompt()
                .user(question)
                .advisors(advisor, questionAnswerAdvisor)
                .system(SYSTEM_PROMPT)
                .call()
                .content();
    }
}

@Slf4j
@Configuration
class VectorStoreInitializer {
    @Bean
    CommandLineRunner runner(TemperatureRepository temperatureRepository, VectorStore vectorStore) {
        return args -> {
            List<Temperature> temperatures = temperatureRepository.findAllTemperatures();
            for (Temperature temperature : temperatures) {
                // Add temperature data to the vector store
                var temperatureDocument = new Document(String.format("TemperatureId %s with sensor %s at %s recorded a temperature of %s at %s",
                                temperature.id(), temperature.sensorRef(), temperature.location(), temperature.temp(), temperature.recordedAt()));
                vectorStore.add(List.of(temperatureDocument));

                log.info("Added temperature data to vector store");
            }
        };
    }
}


@Repository
interface TemperatureRepository extends ListCrudRepository<Temperature, Long> {
    @Query("""
        SELECT id, senssor_ref as sensorRef, location, temp, recorded_at as recordedAt FROM temperature
    """)
    List<Temperature> findAllTemperatures();
}

record Temperature(
        @Id
        long id,
        String sensorRef,
        String location,
        BigDecimal temp,
        Instant recordedAt
) {
}