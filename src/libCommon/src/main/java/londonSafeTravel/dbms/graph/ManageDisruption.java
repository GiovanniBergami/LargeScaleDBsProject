package londonSafeTravel.dbms.graph;

import londonSafeTravel.schema.GeoFactory;
import londonSafeTravel.schema.Location;
import londonSafeTravel.schema.graph.Disruption;
import londonSafeTravel.schema.graph.Point;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class ManageDisruption {
    private final Driver driver;

    public ManageDisruption(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public ManageDisruption(Driver driver) {
        this.driver = driver;
    }

    private static final Query FIND_ACTIVE_DISRUPTIONS = new Query("MATCH(d: Disruption) RETURN d");


    public List<Disruption> findDisruption() {
        ArrayList<Disruption> disruptions= new ArrayList<>();
        try (Session session = driver.session()) {
            var res= session.run(FIND_ACTIVE_DISRUPTIONS);

            res.forEachRemaining(record -> {
                var d = new Disruption();
                d.id = record.get("d").get("id").asString();
                d.centrum = GeoFactory.fromNeo4j(record.get("d").get("centrum").asPoint());
                d.radius = record.get("d").get("radius").asDouble();
                d.severity = record.get("d").get("severity").asString();
                d.ttl = record.get("d").get("ttl").asLong();

                disruptions.add(d);
            });
        }
        return disruptions;
    }
}
