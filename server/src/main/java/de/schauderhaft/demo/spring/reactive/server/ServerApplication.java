package de.schauderhaft.demo.spring.reactive.server;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}


	@RestController
	@RequestMapping(value = "persons", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	@RequiredArgsConstructor
	private static class PersonRestController {

		private final MongoTemplate template;

		@PostConstruct
		void afterInit(){
		    // this is necessary in order for tailable cursors to work.
			template.createCollection(Person.class, CollectionOptions.empty().capped().maxDocuments(10000).size(10000));
		}


		@RequestMapping( method = RequestMethod.POST)
		Mono<Person> addPerson(@RequestBody String name) {
			System.out.println("Received a person with name " + name);
			return Mono.just(createPerson(name));
		}

		@RequestMapping( method = RequestMethod.GET)
		Flux<Person> persons() {

			return Flux.just(createPerson("Oliver"), createPerson("Mark"), createPerson("Christoph")).delayElements(Duration.ofSeconds(1));
		}

		private Person createPerson(String name) {

			Person person = new Person(null);
			person.setName(name);
			return person;
		}
	}

	@Document
	@Data
	private static class Person {

		@Id final String id;

		String name;

		List<String> olderPersonsOfSameName = new ArrayList<>();
	}
}
