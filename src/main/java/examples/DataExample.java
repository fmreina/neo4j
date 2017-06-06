package examples;

import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;

import org.neo4j.driver.v1.Record;

import application.Operation;
import entity.Person;

@NoArgsConstructor
public class DataExample {

	public static void firstSetup(Operation op) {
		clearDB(op);
		createRootNode(op);
		insertPeople(op);
		createRelationshipG1(op);
		createRelationshipG2(op);
		connectRootToAll(op);
	}

	private static void clearDB(Operation op) {

		System.out.println("Clearing the DB");
		op.clearDB();
	}

	private static void createRootNode(Operation op) {
		String name = "Root";
		String attributesForHashing = name;

		String hash = op.getHash(attributesForHashing); // the hash value is calculated over the concatenation of all attributes of the node
		String chash = op.getHash(hash); // when a node is created it has no neighbors yet, so it's cHash is the hash of only it's Hash value

		List<Record> result = op.runGenericExpression("CREATE (a:ROOT{name: \"" + name + "\", hash: \"" + hash + "\", chash: \"" + chash + "\"}) RETURN id(a), a.name");
		result.forEach(record -> System.out.println("Node id(" + record.get("id(a)", 0) + ") name: " + record.get("a.name", "")));
	}

	private static void connectRootToAll(Operation op) {
		List<Record> nodes = op.runGenericExpression("MATCH (a) RETURN a.name");
		nodes.forEach(name -> {
			List<Record> result = op.runGenericExpression("match (a:ROOT), (b) with a, b where a<>b create unique (a)-[r:HAS_NODE]->(b)");
			result.forEach(record -> System.out.println(record));
		});
	}

	private static void insertPeople(Operation op) {

		System.out.println("Inserting people");
		getPeople().forEach(person -> {
			long id = op.addPerson(person);
			System.out.println("new node: id(" + id + ")");
		});

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

		people.add(new Person("Junior", "Cavalcante", 30, 1.73, 80));
		people.add(new Person("Carla", "Santos", 18, 1.76, 72));
		people.add(new Person("Flavia", "Gutemberg", 24, 1.75, 68));
		people.add(new Person("Regina", "Carvalho", 31, 1.75, 72));
		people.add(new Person("Lucas", "Oliveira", 42, 1.8, 67));
		people.add(new Person("Rafaela", "Marques", 27, 1.77, 76));
		people.add(new Person("Eduardo", "Silva", 20, 1.64, 70));

		return people;
	}

	private static void createRelationshipG1(Operation op) {

		System.out.println("Creating relationships");
		//@formatter:off
		List<Record> relation = op.runGenericExpression(
				"MATCH (fab:Person) , (adr:Person), (keo:Person), (ari:Person), (dav:Person), (bru:Person) , (lua:Person) "
				+ "WHERE fab.name = \"Fabio\" AND adr.name = \"Adriana\" AND keo.name = \"Keoma\" AND ari.name = \"Ari\" "
				+ "AND dav.name = \"David\" AND bru.name = \"Brunna\" AND lua.name = \"Luana\" "
				+ "CREATE "

				+ "(fab)-[r1:SIBLING_OF{name: \"sibling_of\"}]->(adr), "
				+ "(adr)-[r2:SIBLING_OF{name: \"sibling_of\"}]->(fab), "

				+ "(fab)-[r3:FRIEND_OF{name: \"friend_of\"}]->(keo), "
				+ "(keo)-[r4:FRIEND_OF{name: \"friend_of\"}]->(fab), "

//				+ "(fab)-[r5:FRIEND_OF{name: \"friend_of\"}]->(lua), "
//				+ "(lua)-[r6:FRIEND_OF{name: \"friend_of\"}]->(fab), "

				+ "(fab)-[r7:FRIEND_OF{name: \"friend_of\"}]->(bru), "
				+ "(bru)-[r8:FRIEND_OF{name: \"friend_of\"}]->(fab), "

				+ "(bru)-[r9:FRIEND_OF{name: \"friend_of\"}]->(lua), "
				+ "(lua)-[r10:FRIEND_OF{name: \"friend_of\"}]->(bru), "

				+ "(keo)-[r11:FRIEND_OF{name: \"friend_of\"}]->(dav), "
				+ "(dav)-[r12:FRIEND_OF{name: \"friend_of\"}]->(keo), "

				+ "(dav)-[r13:FRIEND_OF{name: \"friend_of\"}]->(ari), "
				+ "(ari)-[r14:FRIEND_OF{name: \"friend_of\"}]->(dav), "

				+ "(adr)-[r15:KNOWS{name: \"knows\"}]->(lua), "
				+ "(lua)-[r16:KNOWS{name: \"knows\"}]->(adr), "

				+ "(adr)-[r17:KNOWS{name: \"knows\"}]->(keo), "
				+ "(keo)-[r18:KNOWS{name: \"knows\"}]->(adr) "

				+ "RETURN "
				+ "fab.name, adr.name, keo.name, lua.name, bru.name, dav.name, ari.name,"
				+ "r1.name, r2.name, r3.name, r4.name, "
//				+ "r5.name, r6.name, "
				+ "r7.name, r8.name, "
				+ "r9.name, r10.name, r11.name, r12.name, r13.name, r14.name, r15.name, "
				+ "r16.name, r17.name, r18.name "
				);
		//@formatter:on

		relation.forEach(record -> System.out.println(record));
	}

	private static void createRelationshipG2(Operation op) {

		System.out.println("Creating relationships");
		//@formatter:off
		List<Record> relation = op.runGenericExpression(
				"MATCH (jun:Person) , (car:Person), (fla:Person), (reg:Person), (luc:Person), (raf:Person) , (edu:Person) "
				+ "WHERE jun.name = \"Junior\" AND car.name = \"Carla\" AND fla.name = \"Flavia\" AND reg.name = \"Regina\" "
				+ "AND luc.name = \"Lucas\" AND raf.name = \"Rafaela\" AND edu.name = \"Eduardo\" "
				+ "CREATE "

				+ "(jun)-[r1:FRIEND_OF{name: \"sibling_of\"}]->(car), "
				+ "(car)-[r2:FRIEND_OF{name: \"sibling_of\"}]->(jun), "

				+ "(jun)-[r3:FRIEND_OF{name: \"friend_of\"}]->(fla), "
				+ "(fla)-[r4:FRIEND_OF{name: \"friend_of\"}]->(jun), "

				+ "(car)-[r5:KNOWS{name: \"friend_of\"}]->(fla), "
//				+ "(fla)-[r6:KNOWS{name: \"friend_of\"}]->(car), "

				+ "(fla)-[r7:KNOWS{name: \"friend_of\"}]->(raf), "
//				+ "(raf)-[r8:KNOWS{name: \"friend_of\"}]->(fla), "

				+ "(raf)-[r9:KNOWS{name: \"friend_of\"}]->(edu), "
//				+ "(edu)-[r10:KNOWS{name: \"friend_of\"}]->/(raf), "

//				+ "(fla)-[r11:KNOWS{name: \"friend_of\"}]->(edu), "
				+ "(edu)-[r12:KNOWS{name: \"friend_of\"}]->(fla), "

				+ "(luc)-[r13:FRIEND_OF{name: \"friend_of\"}]->(reg), "
				+ "(reg)-[r14:FRIEND_OF{name: \"friend_of\"}]->(luc), "

				+ "(luc)-[r15:KNOWS{name: \"knows\"}]->(edu), "
//				+ "(edu)-[r16:KNOWS{name: \"knows\"}]->(car), "

//				+ "(edu)-[r17:KNOWS{name: \"knows\"}]->(reg), "
				+ "(reg)-[r18:KNOWS{name: \"knows\"}]->(edu) "

				+ "RETURN "
				+ "jun.name, car.name, fla.name, edu.name, raf.name, luc.name, reg.name,"
				+ "r1.name, r2.name, r3.name, r4.name, "
				+ "r5.name, "
//				+ "r6.name, "
				+ "r7.name, "
//				+ "r8.name, "
				+ "r9.name, "
//				+ "r10.name, r11.name, "
				+ "r12.name, r13.name, r14.name, r15.name, "
//				+ "r16.name, r17.name, "
				+ "r18.name "
				);
		//@formatter:on

		relation.forEach(record -> System.out.println(record));
	}
}
