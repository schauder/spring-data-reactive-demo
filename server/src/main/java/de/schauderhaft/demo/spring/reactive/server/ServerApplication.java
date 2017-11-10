package de.schauderhaft.demo.spring.reactive.server;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableReactiveMongoRepositories(considerNestedRepositories = true)
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@RestController
	@RequestMapping(value = "persons", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	@RequiredArgsConstructor
	private static class PersonRestController {

		private final MongoTemplate template;
		private final ReactivePersonRepository repository;

		@PostConstruct
		void afterInit() {
			// this is necessary in order for tailable cursors to work.
			template.createCollection(Person.class, CollectionOptions.empty().capped().maxDocuments(10000).size(10000));
		}

		@RequestMapping(method = RequestMethod.POST)
		Mono<Person> addPerson(@RequestBody String name) {
			System.out.println("Received a person with name " + name);
			return repository.findByName(name).map(Person::getId).collectList().map(l -> {
				Person person = createPerson(name);
				person.setOlderPersonsOfSameName(l);
				return person;
			}).flatMap(repository::save);
		}

		@RequestMapping(method = RequestMethod.GET)
		Flux<Person> persons() {

			return repository.findAllTailableBy();
		}

		private Person createPerson(String name) {

			Person person = new Person(null);
			person.setName(name);
			return person;
		}
	}

	interface ReactivePersonRepository extends ReactiveCrudRepository<Person, String> {

		Flux<Person> findByName(String name);

		@Tailable
		Flux<Person> findAllTailableBy();
	}

	@Document
	@Data
	private static class Person {

		@Id final String id;

		String name;

		List<String> olderPersonsOfSameName = new ArrayList<>();
	}
}
