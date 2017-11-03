package de.schauderhaft.demo.spring.reactive.client;

import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class LoadApplication {

	private static WebClient client = WebClient.create("http://localhost:8080/");

	public static void main(String[] args) {

		while (true) {
			client //
					.post() //
					.uri(builder -> builder.path("/persons").build()) //
					.body(Mono.just(Names.randomName()), String.class) //
					.retrieve() //
					.bodyToFlux(Person.class) //
					.doOnNext(System.out::println) //
					.blockLast();
		}
	}
}
