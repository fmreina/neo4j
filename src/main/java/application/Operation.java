package application;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.List;

import lombok.NoArgsConstructor;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import cryptography.HashGeneretor;
import entity.Person;
import entity.RelationshipType;

@NoArgsConstructor
public class Operation extends BaseApplication {

	public List<Record> runGenericExpression(String expression) {

		try (Session session = this.driver.session()) {

			return session.writeTransaction(new TransactionWork<List<Record>>() {

				@Override
				public List<Record> execute(Transaction transaction) {
					StatementResult result = transaction.run(expression);
					return result.list();
				}
			});
		}
	}

	public void clearDB() {
		try (Session session = this.driver.session()) {
			session.writeTransaction(new TransactionWork<Void>() {
				@Override
				public Void execute(Transaction transaction) {
					transaction.run("MATCH (a) OPTIONAL MATCH (a)-[r]->() DELETE a, r");
					return null;
				}
			});
		}
	}

	public void addOneWayRelation(String nameA, String labelA, String nameB, String labelB, RelationshipType relation) {
		try (Session session = this.driver.session()) {
			session.writeTransaction(new TransactionWork<Void>() {
				@Override
				public Void execute(Transaction transaction) {
					//@formatter:off;
					//FIXME: make it generic
					String statement = "MATCH (a:"+labelA+") , (b:"+labelB+") "
							+ "WHERE a.name = \""+nameA+"\" AND b.name = \""+nameB+"\" "
							+ "CREATE (a)-[r:"+relation.getLabel()+"{name: \""+relation.getName()+"\"}]->(b) "
									+ "RETURN a.name, r.name, b.name";
					//@formatter:on

					StatementResult result = transaction.run(statement);
					result.list().forEach(r -> System.out.println(r));
					return null;
				}
			});
			this.updateCHash(nameA, true);
		}
	}

	public void addTwoWayRelation(String nameA, String labelA, String nameB, String labelB, RelationshipType relation) {
		try (Session session = this.driver.session()) {
			session.writeTransaction(new TransactionWork<Void>() {
				@Override
				public Void execute(Transaction transaction) {
					//@formatter:off;
					//FIXME: make it generic
					String statement = "MATCH (a:"+labelA+") , (b:"+labelB+") "
							+ "WHERE a.name = \""+nameA+"\" AND b.name = \""+nameB+"\" "
							+ "CREATE (a)-[r1:"+relation.getLabel()+"{name: \""+relation.getName()+"\"}]->(b), "
									+ "(b)-[r2:"+relation.getLabel()+"{name: \""+relation.getName()+"\"}]->(a)"
									+ "RETURN a.name, r1.name, b.name, r2.name";
					//@formatter:on

					StatementResult result = transaction.run(statement);
					result.list().forEach(r -> System.out.println(r));
					return null;
				}
			});
			this.updateCHash(nameA, true);
		}
	}

	public void updateCHash(String name, boolean checkNeighbors) {
		// System.out.println("Discover all neighbors");
		List<Record> list = this.getNeighborsOf(name);

		// System.out.println("Collect their hashes");
		String aHash = list.get(0).get("a.hash", "");
		String temp = "";
		String order = "";
		for (Record item : list) {
			temp += item.get("b.hash", "");
			order += item.get("b.name", "");
		}

		// System.out.println("Calculate CHash");
		String cHash = this.getHash(aHash + temp);

		// System.out.println("Update CHash...");
		this.runGenericExpression("MATCH (a {name:\"" + name + "\"}) SET a.chash = \"" + cHash + "\"");

		order = list.get(0).get("a.name", "") + order;
		System.out.println("cHash updated : Node " + name + " : order " + order + " : cHash " + cHash);

		// System.out.println("Repeat for each neighbor");
		if (checkNeighbors) {
			list.forEach(item -> this.updateCHash(item.get("b.name", ""), false));
		}
	}

	public void updateAllCHashes() {
		List<Record> nodes = this.runGenericExpression("MATCH (a) RETURN a.name ORDER BY id(a)");

		nodes.forEach(node -> {
			String name = node.get("a.name", "");
			this.updateCHash(name, true);
		});
	}

	public List<Record> getNeighborsOf(String name) {

		try (Session session = this.driver.session()) {

			return session.writeTransaction(new TransactionWork<List<Record>>() {

				@Override
				public List<Record> execute(Transaction transaction) {
					String statement = "MATCH (a{name:\"" + name + "\"}) OPTIONAL MATCH (a)<-[r]->(b) RETURN a.name, a.hash, b.name, b.hash ORDER BY id(b)";
					StatementResult result = transaction.run(statement);
					return result.list();
				}
			});
		}
	}

	// FIXME: make it addNode (generic)
	public long addPerson(final Person person) {
		try (Session session = this.driver.session()) {
			session.writeTransaction(new TransactionWork<Void>() {
				@Override
				public Void execute(Transaction transaction) {
					return Operation.this.createPersonNode(transaction, person);
				}
			});
			return session.readTransaction(new TransactionWork<Long>() {
				@Override
				public Long execute(Transaction transaction) {
					return Operation.this.matchPersonNode(transaction, person);
				}
			});
		}
	}

	// FIXME: make it createNode (generic)
	private Void createPersonNode(Transaction transaction, Person person) {
		String attributesForHashing = person.getName() + person.getSurname() + String.valueOf(person.getAge()) + String.valueOf(person.getHeight())
				+ String.valueOf(person.getWeight());

		String hash = this.getHash(attributesForHashing); // the hash value is calculated over the concatenation of all attributes of the node
		String chash = this.getHash(hash); // when a node is created it has no neighbors yet, so it's cHash is the hash of only it's Hash value

		//@formatter:off
		transaction.run("CREATE (a:Person {name: $name, surname: $surname, age:$age, height: $height, weight: $weight, hash: $hash, chash: $chash})",
				parameters("name", person.getName(), "surname", person.getSurname(), "age", person.getAge(), "height",
						person.getHeight(), "weight", person.getWeight(), "hash", hash, "chash", chash));
		//@formatter:on
		return null;
	}

	// FIXME: make it matchNode (generic)
	public long matchPersonNode(Transaction transaction, Person person) {
		StatementResult result = transaction.run("MATCH (a:Person {name: $name}) RETURN id(a)", parameters("name", person.getName()));
		return result.single().get(0).asLong();
	}

	public String getHash(String msg) {
		String hashed = HashGeneretor.hashPassword(msg);
		// return hashed; // just for testing
		return msg;
	}
}
