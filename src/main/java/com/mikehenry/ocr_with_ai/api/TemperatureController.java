package com.mikehenry.ocr_with_ai.api;

import com.mikehenry.ocr_with_ai.domain.entity.Temperature;
import com.mikehenry.ocr_with_ai.domain.repository.TemperatureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
            Below is the context of the temperature readings:
           """;

    // to be able to remember previous conversations with a user, we need to momorize the chat history
    private final Map<String, PromptChatMemoryAdvisor> advisorMap = new ConcurrentHashMap<>();

    private List<Temperature> temperaturesData;

    @GetMapping("/{user}/chat/inquiries")
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
                .advisors(questionAnswerAdvisor)
                .system(SYSTEM_PROMPT)
                .call()
                .content();
    }

    /**
     * A simple endpoint to inquire about temperature data. The data is fetched from the repository,
     * and a context is built
     */
    @GetMapping("/{user}/inquiries")
    public String inquire(@PathVariable String user, @RequestParam String question) {
        temperaturesData = temperatureRepository.findAll();  // fetch raw data

        log.info("temperatures: {}", temperaturesData);

        // build context
        String context = temperaturesData.stream()
                .map(t -> String.format(
                        "The sensor with reference %s recorded the temperature in degrees %s°C on the date %s for the location %s",
                        t.sensorRef(), t.temp(), t.recordedAt(), t.location()
                ))
                .collect(Collectors.joining("\n"));

        // full system prompt
        String prompt = SYSTEM_PROMPT.concat(context);

//        var advisor = advisorMap.computeIfAbsent( user,
//        advisorMemory -> PromptChatMemoryAdvisor.builder(
//                MessageWindowChatMemory.builder()
//                        .maxMessages(10)
//                        .chatMemoryRepository(chatMemoryRepository)
//                        .build())
//                .build());

        // call LLM
        return chatClient.prompt()
                .system(prompt)
//                .advisors(advisor)
                .user(question)
                .call()
                .content();
    }

}

//@Slf4j
//@Configuration
//class VectorStoreInitializer {
//    @Bean
//    CommandLineRunner runner(TemperatureRepository temperatureRepository, VectorStore vectorStore) {
//        return args -> {
//            List<Temperature> temperatures = temperatureRepository.findAllTemperatures();
//            for (Temperature temperature : temperatures) {
//                // Add temperature data to the vector store
//                var temperatureDocument = new Document(String.format("TemperatureId %s with sensor %s at %s recorded a temperature of %s at %s",
//                                temperature.id(), temperature.sensorRef(), temperature.location(), temperature.temp(), temperature.recordedAt()));
//                vectorStore.add(List.of(temperatureDocument));
//
//                log.info("Added temperature data to vector store");
//            }
//        };
//    }
//}