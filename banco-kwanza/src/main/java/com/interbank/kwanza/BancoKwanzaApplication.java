package com.interbank.kwanza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BancoKwanzaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BancoKwanzaApplication.class, args);
	}

}
