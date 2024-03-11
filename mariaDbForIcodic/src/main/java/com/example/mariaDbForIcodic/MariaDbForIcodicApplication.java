package com.example.mariaDbForIcodic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.mariaDbForIcodic.DBO.DataReceiver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@SpringBootApplication
public class MariaDbForIcodicApplication {

	public static void main(String[] args) throws JsonMappingException, JsonProcessingException {
		SpringApplication.run(MariaDbForIcodicApplication.class, args);

		//DataReceiver tesDataReceiver = new DataReceiver();
		//var icodic = "{\"name\": \"testtest\", \"icodic\": \"261684986\"}";
		//tesDataReceiver.receiveAndInsertData(icodic);
		
	}

}
