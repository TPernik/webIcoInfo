package com.example.mariaDbForIcodic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@SpringBootApplication
@EnableScheduling
public class MariaDbForIcodicApplication {

	public static void main(String[] args) throws JsonMappingException, JsonProcessingException {
		SpringApplication.run(MariaDbForIcodicApplication.class, args);
	}

}
