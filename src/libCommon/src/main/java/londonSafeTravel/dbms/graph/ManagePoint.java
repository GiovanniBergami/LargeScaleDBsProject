package londonSafeTravel.dbms.graph;
import londonSafeTravel.schema.Location;
import londonSafeTravel.schema.graph.Point;
import org.neo4j.driver.Driver;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import static org.neo4j.driver.Values.parameters;

public class ManagePoint {
    public static class InsertGraph {
        private final Driver driver;

        public InsertGraph(String uri, String user, String password){
            driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        }

        public void addNode(final Point p){
            try(Session session = driver.session()){
                session.writeTransaction(tx-> createPlaceNode(tx,p.id, p.location.getLatitude(), p.location.getLongitude()));
            }
        }

        public Void createPlaceNode(Transaction tx, Integer id, double lat, double longitude){
            try( Session session=driver.session() ) {
                tx.run("CREATE (p:Point{id:$id, lat:$lat, longitude:$longitude})", parameters("id", id, "lat", lat, "longitude", longitude));
            }
            return null;
        }

        public static void main(String[] argv){
            InsertGraph test= new InsertGraph("neo4j://localhost:7687", "neo4j", "pass");
            double latitude=100.00;
            double longitude=200.00;
            int id=1;
            Point p=new Point(id,latitude,longitude);
            test.addNode(p);
        }
    }
}
