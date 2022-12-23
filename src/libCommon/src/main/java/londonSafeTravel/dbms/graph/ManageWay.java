package londonSafeTravel.dbms.graph;

import londonSafeTravel.schema.graph.Point;
import londonSafeTravel.schema.graph.Way;
import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

public class ManageWay {
    public static class InsertWayIntoGraph{
        private final Driver driver;

        public InsertWayIntoGraph(String uri, String user, String password) {
            driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        }

        public void addWay(final Way w){
            try(Session session = driver.session()){
                session.writeTransaction(tx-> createWay(tx, w.getId(), w.getP1(), w.getP2(), w.getName(), w.getMaxSpeed()));
            }
        }
        public Void createWay(Transaction tx, long id, Point p1, Point p2, String name, int maxSpeed){
            try( Session session=driver.session() ) {
                tx.run("MATCH (p1:Point),(p2:Point) WHERE p1.id=$id1 AND p2.id=$id2 CREATE (p1)-[r:TO {name: $name, maxSpeed: $maxSpeed}]->(p2) RETURN type(r)", parameters("id1",p1.getId(), "id2", p2.getId(), "name",name,"maxSpeed", maxSpeed));
            }
            return null;
        }
    }
    public static void main(String[] argv){
        InsertWayIntoGraph test= new InsertWayIntoGraph("neo4j://localhost:7687", "neo4j", "pass");
        Point p1=new Point(2,101.00,201.00);
        Point p2=new Point(1,100.00,200.00);
        Way w=new Way(1,p1,p2,"Via Diotisalvi", 50);
        test.addWay(w);
    }
}
