package londonSafeTravel.dbms.graph;

import londonSafeTravel.schema.graph.Point;
import londonSafeTravel.schema.graph.Way;

import org.neo4j.driver.*;

import java.util.Collection;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class ManageWay {

        private final Driver driver;

        public ManageWay(String uri, String user, String password) {
            driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        }

        public void addWays(Collection<Way> ways) {
            try(var session = driver.session()){
                session.executeWriteWithoutResult(tx -> createWays(tx, ways));
            }
        }
        private void createWays(TransactionContext tx, Collection<Way> ways){
            ways.forEach(way -> {
                tx.run(
                        "MERGE (p1: Point {id: $id1})" +
                                "MERGE (p2: Point {id: $id2})" +
                                "MERGE (p1)-[:CONNECTS {" +
                                "   name: $name, class: $class, maxspeed: $speed," +
                                "   crossTimeFoot: $crossFoot, crossTimeBicycle: $crossBicycle, crossTimeMotorVehicle: $crossMotorVehicle" +
                                "}]->(p2)"+
                                "MERGE (p2)-[:CONNECTS {" +
                                "   name: $name, class: $class, maxspeed: $speed," +
                                "   crossTimeFoot: $crossFoot, crossTimeBicycle: $crossBicycle, crossTimeMotorVehicle: $crossMotorVehicle" +
                                "}]->(p1)"+
                                "ON CREATE SET p1.coord = point({longitude: $lon1, latitude: $lat1})" +
                                "ON CREATE SET p1.lat = $lat1 " +
                                "ON CREATE SET p1.lon = $lon1 " +
                                "ON CREATE SET p2.coord = point({longitude: $lon2, latitude: $lat2})" +
                                "ON CREATE SET p2.lat = $lat2, p2.lon = $lon2", // @TODO Se oneway=yes and foot=no allora non serve l'inverso!
                        parameters(
                                "id1", way.p1.getId(),
                                "lat1", way.p1.getLocation().getLatitude(),
                                "lon1", way.p1.getLocation().getLongitude(),
                                "id2", way.p2.getId(),
                                "lat2", way.p2.getLocation().getLatitude(),
                                "lon2", way.p2.getLocation().getLongitude(),
                                //"wid", way.id,
                                "name", way.name,
                                "crossFoot", way.crossTimes.get("foot"),
                                "crossBicycle", way.crossTimes.get("bicycle"),
                                "crossMotorVehicle", way.crossTimes.get("motor_vehicle"),
                                "speed", way.maxSpeed,
                                "class", way.roadClass
                        )
                );
            });


            // Perché ci stanno due try sulla session innestate???
//            try( Session session=driver.session() ) {
//                tx.run(
//                        "MATCH (p1:Point),(p2:Point) WHERE p1.id=$id1 AND p2.id=$id2" +
//                                "CREATE (p1)-[r:TO {name: $name, maxSpeed: $maxSpeed}]->(p2) RETURN type(r)",
//                        parameters(
//                                "id1",p1.getId(), "id2", p2.getId(),
//                                "name",name,"maxSpeed", maxSpeed)
//                );
//            }
        }

    private Collection<Way> elementsInGivenArea(double maxLat, double maxLon, double minLat, double minLon){
        try(Session session = driver.session()){
            return session.readTransaction((TransactionWork<List<Way>>)tx->{
                Result result=tx.run("WITH" +
                        "  point({longitude: $minLon, latitude: $minLat}) AS lowerLeft," +
                        "  point({longitude: $maxLon, latitude: $maxLat}) AS upperRight" +
                        "MATCH (p:Point)" +
                        "WHERE point.withinBBox(p.coord, lowerLeft, upperRight)" +
                        "MATCH(p)-[w]->(q:Point)"+
                        "RETURN p,q,w", parameters("minLon",minLon, "minLat",minLat, "maxLon",maxLon, "maxLat", maxLat));
                return null;
            }) ;
        }
    }

    public static void main(String[] argv){
        ManageWay test= new ManageWay("neo4j://localhost:7687", "neo4j", "pass");
        Point p1=new Point(2,101.00,201.00);
        Point p2=new Point(1,100.00,200.00);
        Way w=new Way();
        w.p1 = p1;
        w.p2 = p2;
        w.name = "Via Diotisalvi";
        test.addWays(List.of(w));
    }
}
