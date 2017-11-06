package de.schauderhaft.demo.spring.reactive.client;

import org.springframework.web.reactive.function.client.WebClient;

public class ClientApplication {

    private static WebClient client = WebClient.create("http://localhost:8080/");

    public static void main(String[] args) {

        client.get() //
                .uri(builder -> builder.path("/persons").build()) //
                .retrieve() //
                .bodyToFlux(Person.class) //
                .doOnNext(System.out::println)
                .blockLast();

    }
}
