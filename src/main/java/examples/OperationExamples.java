package examples;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.List;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import application.BaseApplication;

public class OperationExamples extends BaseApplication {

	public OperationExamples() {
		super();
	}

	public OperationExamples(String uri, String user, String password) {
		super(uri, user, password);
	}

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

	public long addPerson(final String name) {
		try (Session session = this.driver.session()) {
			session.writeTransaction(new TransactionWork<Void>() {
				@Override
				public Void execute(Transaction transaction) {
					return createPersonNode(transaction, name);
				}
			});
			return session.readTransaction(new TransactionWork<Long>() {
				@Override
				public Long execute(Transaction transaction) {
					return matchPersonNode(transaction, name);
				}
			});
		}
	}

	private static Void createPersonNode(Transaction transaction, String name) {
		transaction.run("CREATE (a:Person {name: $name})", parameters("name", name));
		return null;
	}

	private static long matchPersonNode(Transaction transaction, String name) {
		StatementResult result = transaction.run("MATCH (a:Person {name: $name}) RETURN id(a)", parameters("name", name));
		return result.single().get(0).asLong();
	}

}
