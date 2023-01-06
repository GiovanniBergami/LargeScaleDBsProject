package londonSafeTravel.dbms.graph;

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
                    "	latitudeProperty: 'lat', " +
                    "	longitudeProperty: 'lon', " +
                    "	relationshipWeightProperty: 'crossTimeMotorVehicle', " +
                    "	relationshipTypes: ['CONNECTS'], " +
                    "	concurrency: 4" +
                    "	}) " +
                    "YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs, path  " +
                    "UNWIND range(0, size(nodes(path)) - 1) AS i " +
                    "RETURN gds.util.asNode(nodeIds[i]).id AS id " +
                    "ORDER BY index"
    );

    public ManageRouting(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public List<Long> route(long start, long end)
    {
        try(var session = driver.session()){
            List<Long> hops = new ArrayList<>();
            var res = session.run(ROUTE_QUERY.withParameters(parameters(
                    "start", start,
                    "end", end
            )));
            res.forEachRemaining(record -> {
                hops.add(record.get("id").asLong());
            });
            return hops;
        }
    }

    //Inserisco una query per trovare il nodo più vicino ad un dato punto. Utile quando l'utente clicca sulla mappa
    //e vogliamo stabilire nodo di partenza e di arrivo.
    private final Query NEAREST_NODE = new Query(
        "MATCH (n:Node)" +
                "RETURN n " +
                "ORDER BY distance(point({latitude: $lat, longitude: $lng}), n.location) "+
                "LIMIT 1"
    );


    public long NearestNode(double lat, double lng){
        try(var session = driver.session()){
            var p = session.run(NEAREST_NODE.withParameters(parameters("lat",lat,"lng",lng)));
            Point p1 = (Point)p;
            return p1.getId();
        }
    }

    public static void main(String argv[])
    {
        ManageRouting test= new ManageRouting("neo4j://localhost:7687", "neo4j", "pass");

        test.route(4835478720L, 389139L).forEach(hop -> {
            System.out.println("id " + hop);
        });
    }

}
