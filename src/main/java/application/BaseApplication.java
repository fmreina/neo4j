package application;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

public abstract class BaseApplication implements AutoCloseable {

	protected final String uri = "bolt://localhost:7687";
	protected final String user = "neo4j";
	protected final String password = "dbneo4j";

	protected final Driver driver;

	public BaseApplication() {
		this.driver = GraphDatabase.driver(this.uri, AuthTokens.basic(this.user, this.password));
	}

	public BaseApplication(String uri, String user, String password) {
		this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}

	@Override
	public void close() throws Exception {
		this.driver.close();
	}
}