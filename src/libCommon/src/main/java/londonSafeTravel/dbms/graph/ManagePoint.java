package londonSafeTravel.dbms.graph;

import londonSafeTravel.schema.Location;
import londonSafeTravel.schema.graph.Disruption;
import londonSafeTravel.schema.graph.Point;
import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

public class ManagePoint {
    private final Driver driver;

    public ManagePoint(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public ManagePoint(Driver driver) {
        this.driver = driver;
    }

    public void addNode(final Point p) {
        try (Session session = driver.session()) {
            session.executeWriteWithoutResult(tx -> createPlaceNode(tx, p.getId(), p.location.getLatitude(), p.location.getLongitude()));
        }
    }

    private static final Query PLACE_NODE = new Query("CREATE (p:Point{id:$id, lat:$lat, longitude:$longitude})");

    private void createPlaceNode(TransactionContext tx, long id, double lat, double longitude) {
        Query q = PLACE_NODE.withParameters(parameters("id", id, "lat", lat, "longitude", longitude));
        tx.run(q);
    }

    private static final Query CREATE_CLOSURE = new Query(
            "MERGE (d:Disruption {id: $id})\n" +
            "SET d.centrum = point({latitude: $lat, longitude: $lon})\n" +
            "SET d.radius = $radius\n" +
            "SET d.severity = $severity\n" +
            "SET d.ttl = $ttl \n" +
            "SET d.category = $category SET d.subCategory = $subcategory \n"+
            "WITH d\n" +
            "MATCH (p: Point)\n" +
            "WHERE point.distance(p.coord, d.centrum) <= d.radius\n" +
            "MERGE (p)-[:IS_DISRUPTED {severity: $severity}]->(d)\n"
    );

    private static final Query FindActiveDisruptions = new Query(
            "MERGE (d:Disruption)" +
                    "return d"
    );

    public void createClosure(Disruption disruption) {
        try (Session session = driver.session()) {
            session.executeWriteWithoutResult(transactionContext -> {
                Query q = CREATE_CLOSURE.withParameters(parameters(
                        "id", disruption.id,
                        "lat", disruption.centrum.getLatitude(),
                        "lon", disruption.centrum.getLongitude(),
                        "radius", disruption.radius,
                        "ttl", disruption.ttl,
                        "severity", disruption.severity,
                        "category", disruption.category,
                        "subcategory", disruption.subCategory
                ));
                transactionContext.run(q);
            });
        }
    }

    private static final Query DELETE_CLOSURE = new Query(
            "MATCH (d:Disruption {id: $id}) " +
                    "DETACH DELETE d"
    );

    public void deleteClosure(String id) {
        try (Session session = driver.session()) {
            session.executeWriteWithoutResult(transactionContext -> {
                Query q = DELETE_CLOSURE.withParameters(parameters(
                        "id", id
                ));
                transactionContext.run(q);
            });
        }
    }


    public static void main(String[] argv) {
        ManagePoint test = new ManagePoint("neo4j://localhost:7687", "neo4j", "pass");
        double latitude = 102.00;
        double longitude = 202.00;
        int id = 3;
        Point p = new Point(id, latitude, longitude);
        test.addNode(p);
    }
}
