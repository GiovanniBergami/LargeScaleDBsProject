package londonSafeTravel.dbms.graph;

import londonSafeTravel.schema.document.ManageDisruption;
import londonSafeTravel.schema.document.PointOfInterestDAO;
import londonSafeTravel.schema.graph.Point;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class ManageRouting {
    private final Driver driver;
    // longitude, latitude (x, y)
    private final Query ROUTE_QUERY = new Query(
            "MATCH (start: Point{id: $start}) " +
                    "MATCH (end: Point{id: $end}) " +
                    "CALL gds.shortestPath.astar.stream('myGraph', { " +
                    "	sourceNode: start, " +
                    "	targetNode: end, " +
                    "	latitudeProperty: 'latitude', " +
                    "	longitudeProperty: 'longitude', " +
                    "	relationshipWeightProperty: 'crossTimeMotorVehicle', " +
                    "	relationshipTypes: ['CONNECTS'], " +
                    "	concurrency: 4" +
                    "	}) " +
                    "YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs, path  " +
                    "UNWIND range(0, size(nodes(path)) - 1) AS i " +
                    "RETURN gds.util.asNode(nodeIds[i]) AS waypoint " +
                    "ORDER BY index"
    );

    public ManageRouting(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public ManageRouting(Driver driver) {
        this.driver = driver;
    }

    public List<Point> route(long start, long end)
    {
        try(var session = driver.session()){
            List<Point> hops = new ArrayList<>();
            var res = session.run(ROUTE_QUERY.withParameters(parameters(
                    "start", start,
                    "end", end
            )));
            res.forEachRemaining(record -> {
                hops.add(new Point(
                        record.get("waypoint").get("id").asLong(),
                        record.get("waypoint").get("coord").asPoint().y(),
                        record.get("waypoint").get("coord").asPoint().x()
                ));
            });
            return hops;
        }
    }

    //Inserisco una query per trovare il nodo più vicino ad un dato punto. Utile quando l'utente clicca sulla mappa
    //e vogliamo stabilire nodo di partenza e di arrivo.
    private final Query NEAREST_NODE = new Query(
        "MATCH (p:Point)" +
                "RETURN p " +
                "ORDER BY point.distance(point({latitude: $lat, longitude: $lng}), p.coord) "+
                "LIMIT 1"
    );


    public Point nearestNode(double lat, double lng){
        try(var session = driver.session()){
            var p = session.run(NEAREST_NODE.withParameters(parameters("lat",lat,"lng",lng)));
            if(!p.hasNext())
                return null;

            var record = p.single().get(0);

            return new Point(
                    record.get("id").asLong(),
                    record.get("coord").asPoint().y(),
                    record.get("coord").asPoint().x()
            );
        }
    }

    public static void main(String argv[])
    {
        ManageRouting test= new ManageRouting("neo4j://localhost:7687", "neo4j", "pass");

        System.out.println(test.nearestNode(0,0));

        test.route(4835478720L, 389139L).forEach(hop -> {
            System.out.println("id " + hop);
        });
    }

}
