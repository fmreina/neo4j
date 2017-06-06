package examples;

import static org.neo4j.driver.v1.Values.parameters;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

public class HelloWorldExample implements AutoCloseable {

	private final Driver driver;

	public HelloWorldExample(String uri, String user, String pass) {

		this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, pass));

	}

	public void greeting(final String msg) {
		try (Session session = this.driver.session()) {
			String greeting = session.writeTransaction(new TransactionWork<String>() {

				@Override
				public String execute(Transaction trans) {

					//@formatter:off
					StatementResult result = trans.run(
							  "CREATE (a:Greeting) "
							+ "SET a.message = $message "
							+ "RETURN a.message + ', from node ' + id(a)",
							parameters( "message", msg) );
					//@formatter:on

					return result.single().get(0).asString();
				}
			});

			System.out.println(greeting);
		}
	}

	@Override
	public void close() throws Exception {

		this.driver.close();

	}

	public static void main(String... args) throws Exception {
		try (HelloWorldExample greeter = new HelloWorldExample("bolt://localhost:7687", "neo4j", "dbneo4j")) {

			greeter.greeting("hello, world!");

		}
	}
}
