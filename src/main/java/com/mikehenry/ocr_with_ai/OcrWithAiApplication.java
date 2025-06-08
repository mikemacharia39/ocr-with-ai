package com.mikehenry.ocr_with_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OcrWithAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OcrWithAiApplication.class, args);
	}

}
