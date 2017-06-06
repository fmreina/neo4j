package examples;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.Record;

import application.Operation;

public class RelationshipExample {

	public static void main(String... args) {
		System.out.println(".:start running:.\n");

		@SuppressWarnings("resource")
		OperationExamples operation = new OperationExamples();

		List<Record> result = operation.runGenericExpression("MATCH (a:Person) RETURN a.name");

		result.forEach(record -> System.out.println(record));

		// create a relationship between the two nodes
		//@formatter:off
		 result = operation.runGenericExpression( "MATCH (fab:Person) , (adr:Person) "
		 										+ "WHERE fab.name = \"Fabio\" "
		 										+ "AND adr.name = \"Adriana\" "
		 										+ "CREATE (fab)-[r:SIBLIN_OF{name: \"siblim_of\"}]->(adr) "
		 										+ "RETURN fab.name, r, adr.name");
		 //@formatter:on

		result.forEach(record -> System.out.println(record));

		//@formatter:off
		// List<Record>
		result = operation.runGenericExpression("MATCH (a)-[r]->(b) "
															+ "RETURN a.name, r.name, b.name");
		//@formatter:on

		result.forEach(record -> System.out.println(record));

		System.out.println("\n.:end running:.");
	}

	public static void createRelationships(Operation op) {
		List<List<Record>> resultSet = new ArrayList<>();
		List<Record> relation;
		//@formatter:off
		{
//		// Fabio sibling_of Adriana
//		relation = op.runGenericExpression( "MATCH (fab:Person) , (adr:Person) "
//		 										+ "WHERE fab.name = \"Fabio\" "
//		 										+ "AND adr.name = \"Adriana\" "
//		 										+ "CREATE (fab)-[r:SIBLING_OF{name: \"sibling_of\"}]->(adr) "
//		 										+ "RETURN fab.name, r, adr.name");
//		resultSet.add(relation);
//
//		// Adriana sibling_of Fabio
//		relation = op.runGenericExpression( "MATCH (fab:Person) , (adr:Person) "
//		 										+ "WHERE fab.name = \"Fabio\" "
//		 										+ "AND adr.name = \"Adriana\" "
//		 										+ "CREATE (adr)-[r:SIBLING_OF{name: \"sibling_of\"}]->(fab) "
//		 										+ "RETURN adr.name, r, fab.name");
//		resultSet.add(relation);
		}
		{
		// Fabio friend_of Keoma
//		relation = op.runGenericExpression( "MATCH (fab:Person) , (keo:Person) "
//		 										+ "WHERE fab.name = \"Fabio\" "
//		 										+ "AND keo.name = \"Keoma\" "
//		 										+ "CREATE (fab)-[r:FRIEND_OF{name: \"friend_of\"}]->(keo) "
//		 										+ "RETURN fab.name, r, keo.name");
//		resultSet.add(relation);

		// Keoma friend_of Fabio
		relation = op.runGenericExpression( "MATCH (ari:Person) , (keo:Person) "
		 										+ "WHERE ari.name = \"Ari\" "
		 										+ "AND keo.name = \"Keoma\" "
		 										+ "CREATE (keo)-[r:FRIEND_OF{name: \"friend_of\"}]->(ari) "
		 										+ "RETURN keo.name, r, ari.name");
		resultSet.add(relation);
		}
		{
		// Keoma friend_of David
		relation = op.runGenericExpression( "MATCH (ari:Person) , (keo:Person) "
		 										+ "WHERE ari.name = \"Ari\" "
		 										+ "AND keo.name = \"Keoma\" "
		 										+ "CREATE (keo)-[r:FRIEND_OF{name: \"friend_of\"}]->(ari) "
		 										+ "RETURN keo.name, r, ari.name");
		resultSet.add(relation);

		// David friend_of Keoma
		relation = op.runGenericExpression( "MATCH (ari:Person) , (keo:Person) "
		 										+ "WHERE ari.name = \"Ari\" "
		 										+ "AND keo.name = \"Keoma\" "
		 										+ "CREATE (keo)-[r:FRIEND_OF{name: \"friend_of\"}]->(ari) "
		 										+ "RETURN keo.name, r, ari.name");
		resultSet.add(relation);
		}
		{
//		// Keoma friend_of Ari
//		relation = op.runGenericExpression( "MATCH (ari:Person) , (keo:Person) "
//		 										+ "WHERE ari.name = \"Ari\" "
//		 										+ "AND keo.name = \"Keoma\" "
//		 										+ "CREATE (keo)-[r:FRIEND_OF{name: \"friend_of\"}]->(ari) "
//		 										+ "RETURN keo.name, r, ari.name");
//		resultSet.add(relation);

		// Ari friend_of Keoma
		relation = op.runGenericExpression( "MATCH (ari:Person) , (keo:Person) "
		 										+ "WHERE ari.name = \"Ari\" "
		 										+ "AND keo.name = \"Keoma\" "
		 										+ "CREATE (ari)-[r:FRIEND_OF{name: \"friend_of\"}]->(keo) "
		 										+ "RETURN keo.name, r, ari.name");
		resultSet.add(relation);
		}

		relation = op.runGenericExpression( "MATCH (bru:Person) , (lua:Person) "
					+ "WHERE bru.name = \"Brunna\" "
					+ "AND lua.name = \"Luana\" "
					+ "CREATE (bru)-[r1:FRIEND_OF{name: \"friend_of\"}]->(lua), "
					+ "(lua)-[r2:FRIEND_OF{name: \"friend_of\"}]->(bru) "
					+ "RETURN lua.name, r1, r2, bru.name");
		resultSet.add(relation);

		//@formatter:on

		resultSet.forEach(item -> item.forEach(record -> System.out.println(record)));
	}

}
