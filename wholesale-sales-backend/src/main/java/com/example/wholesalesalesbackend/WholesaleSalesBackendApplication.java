package com.example.wholesalesalesbackend;

import java.time.format.DateTimeFormatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@SpringBootApplication(scanBasePackages = { "com.example.wholesalesalesbackend", "com.service" })
public class WholesaleSalesBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(WholesaleSalesBackendApplication.class, args);
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer customizer() {
		return builder -> builder
				.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
				.deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
	}

}