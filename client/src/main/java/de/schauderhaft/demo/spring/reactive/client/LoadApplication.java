package de.schauderhaft.demo.spring.reactive.client;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class LoadApplication {

    private static WebClient client = WebClient.create("http://localhost:8080/");

    public static void main(String[] args) {

        Flux.<String>generate(s -> s.next(Names.randomName())).delayElements(Duration.ofMillis(500)).
                flatMap(LoadApplication::submitPerson, 5)
                .blockLast();
    }

    static Flux<Person> submitPerson(String name) {

        return client //
                .post() //
                .uri(builder -> builder.path("/persons").build()) //
                .body(Mono.just(name), String.class) //
                .retrieve() //
                .bodyToFlux(Person.class) //
                .doOnNext(System.out::println);

    }
}
