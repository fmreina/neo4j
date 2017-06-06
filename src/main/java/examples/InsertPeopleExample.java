package examples;

import java.util.ArrayList;
import java.util.List;

import entity.Person;

public class InsertPeopleExample {

	public static void main(String... args) {
		System.out.println("start running");

		String url = "bolt://localhost:7687";
		String user = "neo4j";
		String password = "dbneo4j";

		@SuppressWarnings("resource")
		OperationExamples operation = new OperationExamples(url, user, password);

		operation.clearDB();

		getPeople().forEach(person -> {
			long insertedId = operation.addPerson(person.getName());
			System.out.println(insertedId);
		});

		System.out.println("end running");
	}

	private static List<Person> getPeople() {
		List<Person> people = new ArrayList<>();
		people.add(new Person("Adriana", "Reina", 24, 1.7, 60));
		people.add(new Person("Ari", "Junior", 23, 1.83, 70));
		people.add(new Person("Brunna", "Keller", 24, 1.75, 60));
		people.add(new Person("David", "Luz", 24, 1.75, 64));
		people.add(new Person("Fabio", "Reina", 26, 1.8, 67));
		people.add(new Person("Keoma", "Pereira", 23, 1.77, 76));
		people.add(new Person("Luana", "Cabral", 25, 1.7, 70));

		return people;
	}
}
