package de.schauderhaft.demo.spring.reactive.client;

import java.time.Duration;

import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class LoadApplication {

	private static WebClient client = WebClient.create("http://localhost:8080/");

	public static void main(String[] args) {

		client //
				.post() //
				.uri(builder -> builder.path("/persons").build()) //
				.body(Mono.just("Jens"), String.class) //
				.retrieve() //
				.bodyToFlux(Person.class) //
				.doOnNext(System.out::println) //
				.blockLast(Duration.ofSeconds(1));
	}
}
