package londonSafeTravel.dbms.graph;

import londonSafeTravel.schema.graph.Point;
import londonSafeTravel.schema.graph.RoutingHop;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.neo4j.driver.Values.parameters;

public class RoutingDAO {
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
                    "	relationshipWeightProperty: $type, " +
                    "	relationshipTypes: ['CONNECTS'], " +
                    "	concurrency: 4" +
                    "	}) " +
                    "YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs, path  " +
                    "UNWIND range(0, size(nodes(path)) - 1) AS i " +
                    "RETURN gds.util.asNode(nodeIds[i]) AS waypoint " +
                    "ORDER BY index"
    );

    public RoutingDAO(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public RoutingDAO(Driver driver) {
        this.driver = driver;
    }

    public List<Point> route(long start, long end, String type)
    {
        if(Objects.equals(type, "car"))
            type = "crossTimeMotorVehicle";
        else if( Objects.equals(type, "bicycle"))
            type = "crossTimeBicycle";
        else if(Objects.equals(type, "foot"))
            type = "crossTimeFoot";

        try(var session = driver.session()){
            List<Point> hops = new ArrayList<>();
            var res = session.run(ROUTE_QUERY.withParameters(parameters(
                    "start", start,
                    "end", end,
                    "type", type
            )));
            res.forEachRemaining(record -> hops.add(new Point(
                    record.get("waypoint").get("id").asLong(),
                    record.get("waypoint").get("coord").asPoint().y(),
                    record.get("waypoint").get("coord").asPoint().x()
            )));
            return hops;
        }
    }

    private final Query ROUTE_1_QUERY = new Query("""
MATCH (s:Point{id: $start})
MATCH (e:Point{id: $end})
CALL londonSafeTravel.route.anytime(s, e, $type, $considerDisruptions, $maxspeed, 12.5)
YIELD index, node, time
RETURN index, node AS waypoint, time
ORDER BY index DESCENDING
""");

    public List<RoutingHop> route1(long start, long end, String type, boolean considerDisruptions)
    {
        double maxspeed = 10.0;
        if(Objects.equals(type, "car")) {
            type = "crossTimeMotorVehicle";
            maxspeed=70.0;
        }
        else if( Objects.equals(type, "bicycle")) {
            type = "crossTimeBicycle";
            maxspeed = 15;
        }
        else if(Objects.equals(type, "foot")) {
            type = "crossTimeFoot";
            maxspeed=4;
        }

        try(var session = driver.session()){
            List<RoutingHop> hops = new ArrayList<>();
            var res = session.run(ROUTE_1_QUERY.withParameters(parameters(
                    "start", start,
                    "end", end,
                    "type", type,
                    "maxspeed", maxspeed,
                    "considerDisruptions", considerDisruptions
            )));
            res.forEachRemaining(record -> hops.add(new RoutingHop(
                    new Point(
                        record.get("waypoint").get("id").asLong(),
                        record.get("waypoint").get("coord").asPoint().y(),
                        record.get("waypoint").get("coord").asPoint().x()
                    ), record.get("time").asDouble()
            )));
            return hops;
        }
    }

    //Inserisco una query per trovare il nodo più vicino ad un dato punto. Utile quando l'utente clicca sulla mappa
    //e vogliamo stabilire nodo di partenza e di arrivo.
    private final Query NEAREST_NODE = new Query(
            """
WITH point({latitude: $lat, longitude: $lng}) AS q
MATCH (p:Point)
MATCH (p)-[w:CONNECTS]->(r:Point)
WHERE point.distance(q, p.coord) < 100 AND
CASE
    WHEN $type = 'foot' THEN w.crossTimeFoot <> Infinity
    WHEN $type = 'bicycle' THEN w.crossTimeBicycle <> Infinity
    WHEN $type = 'car' THEN w.crossTimeMotorVehicle <> Infinity
END
RETURN p, point.distance(q, p.coord)
ORDER BY point.distance(q, p.coord) LIMIT 1
"""
    );

    public Point nearestNode(double lat, double lng){
        return nearestNode(lat, lng, "car");
    }

    public Point nearestNode(double lat, double lng, String type){
        try(var session = driver.session()){
            var p = session.run(
                    NEAREST_NODE.withParameters(
                            parameters("lat",lat,
                                    "lng", lng, "type", type)));
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


    public static void main(String[] argv)
    {
        RoutingDAO test= new RoutingDAO("neo4j://localhost:7687", "neo4j", "pass");

        System.out.println(test.nearestNode(0,0));

        test.route1(4835478720L, 389139L, "car", true).forEach(hop -> System.out.println("id " + hop.point));
    }
}
