package com.saguro.rapid.configserver;

import java.security.NoSuchAlgorithmException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RapidConfigServerApplication {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		SpringApplication.run(RapidConfigServerApplication.class, args);
	}

}
