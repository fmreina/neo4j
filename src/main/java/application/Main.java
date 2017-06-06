package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Record;

import cryptography.HashGeneretor;
import entity.Person;
import entity.RelationshipType;
import examples.DataExample;

public class Main {

	private static boolean RUN_SETUP = false;
	private static boolean RUN_ADD_NODE = false;
	private static boolean RUN_ADD_RELATIONSHIP = false;
	private static boolean RUN_GET_NEIGHBORS = false;
	private static boolean RUN_UPDATE_CHASH = false;
	private static boolean RUN_PRINT = true;

	public static void main(String... args) {
		System.out.println("Start running");

		Operation op = new Operation();
		List<Record> result = new ArrayList<>();

		if (RUN_SETUP) {
			DataExample.firstSetup(op);
			System.out.println("Done Setup\n");
		}

		if (RUN_ADD_NODE) {
			// test to add one person/new node
			op.addPerson(new Person("João", "Silva", 28, 1.80, 78));
			result = op.runGenericExpression("MATCH (a) WHERE a.name = \"João\" OPTIONAL MATCH (a)-[r]->() RETURN a.name, a.surname, a.age, a.height, a.weight, a.mac");
		}

		if (RUN_ADD_RELATIONSHIP) {
			// test to add relationships
			op.addOneWayRelation("João", "Person", "Luana", "Person", RelationshipType.KNOWS);
			op.addTwoWayRelation("Ari", "Person", "Brunna", "Person", RelationshipType.KNOWS);
			result = op.runGenericExpression("MATCH (a) RETURN a.name, a.surname, a.age, a.height, a.weight, a.mac");
		}

		if (RUN_GET_NEIGHBORS) {
			// test to get neighbors
			result = op.getNeighborsOf("Fabio");
			String aHash = result.get(0).get("a.hash", "");
			String temp = "";
			System.out.println("aHash = " + aHash);
			for (Record item : result) {
				Map<String, Object> map = item.asMap();
				aHash = (String) map.get("a.hash");
				temp += map.get("b.hash");
				System.out.println("aHash = " + aHash);
				System.out.println("bHash = " + map.get("b.hash"));
				System.out.println("temp = " + temp);
			}
			System.out.println("aHash = " + aHash);
			System.out.println("temp = " + temp);
			String aCHash = HashGeneretor.hashPassword(aHash + temp);
			System.out.println(aCHash);
			System.out.println("\n");
		}

		if (RUN_UPDATE_CHASH) {
			//// test update
			// op.updateCHash("Fabio", true);
			op.updateAllCHashes();
		}

		if (RUN_PRINT) {
			// print
			// result = op.runGenericExpression("MATCH (a)-[r]->(b) " + "RETURN a.name, r.name, b.name");
			result = op.runGenericExpression("MATCH (a) RETURN id(a), a.name, a.surname, a.age, a.height, a.weight, a.hash, a.chash");
			result.forEach(record -> System.out.println(record));
		}

		System.out.println("\nEnd running");
	}
}
