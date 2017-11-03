package de.schauderhaft.demo.spring.reactive.client;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Data;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		WebClient client = WebClient.create("http://localhost:8080/");
		client.get() //
				.uri(builder -> builder.path("/persons").build()) //
				.retrieve() //
				.bodyToFlux(Person.class) //
				.doOnNext(System.out::println)
				.blockLast(Duration.ofSeconds(5))
		;
	}

	@Data
	private static class Person {
		String id;
		String name;
		List<String> olderPersonsOfSameName = new ArrayList<>();
	}
}
