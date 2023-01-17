package londonSafeTravel.dbms.graph;

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

    public static void main(String[] argv) {
        ManagePoint test = new ManagePoint("neo4j://localhost:7687", "neo4j", "pass");
        double latitude = 102.00;
        double longitude = 202.00;
        int id = 3;
        Point p = new Point(id, latitude, longitude);
        test.addNode(p);
    }
}
