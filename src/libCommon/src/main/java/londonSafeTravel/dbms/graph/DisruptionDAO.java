package londonSafeTravel.dbms.graph;

import londonSafeTravel.schema.GeoFactory;
import londonSafeTravel.schema.graph.Disruption;
import org.neo4j.driver.*;
import org.neo4j.driver.async.AsyncSession;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.neo4j.driver.Values.parameters;

public class DisruptionDAO {
    private final Driver driver;

    public DisruptionDAO(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public DisruptionDAO(Driver driver) {
        this.driver = driver;
    }

    private static final Query CREATE_CLOSURE = new Query(
            """
                    MERGE (d:Disruption {id: $id})
                    SET d.centrum = point({latitude: $lat, longitude: $lon})
                    SET d.radius = $radius
                    SET d.severity = $severity
                    SET d.ttl = $ttl
                    SET d.category = $category SET d.subCategory = $subcategory
                    SET d.comment = $comment
                    SET d.update = $update SET d.updateTime = $updateTime
                    SET d.closed = $closed
                    WITH d
                    MATCH (p: Point)
                    WHERE point.distance(p.coord, d.centrum) <= d.radius
                    MERGE (p)-[:IS_DISRUPTED {severity: $severity}]->(d)
                    """
    );

    //private static final Query FindActiveDisruptions = new Query(
    //        "MERGE (d:Disruption)" +
    //                "return d"
    //);

    public void createClosureAsync(Disruption disruption) {
        AsyncSession session = driver.session(AsyncSession.class);
        session.executeWriteAsync(transactionContext -> {
                    Query q = CREATE_CLOSURE.withParameters(parameters(
                            "id", disruption.id,
                            "lat", disruption.centrum.getLatitude(),
                            "lon", disruption.centrum.getLongitude(),
                            "radius", disruption.radius,
                            "ttl", disruption.ttl,
                            "severity", disruption.severity,
                            "category", disruption.category,
                            "subcategory", disruption.subCategory,
                            "comment", disruption.comment,
                            "closed", disruption.closed,
                            "update", disruption.update,
                            "updateTime", disruption.updateTime.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime()
                    ));
                    return transactionContext.runAsync(q);
        }
        );
    }

    public void createClosure(Disruption disruption) {
        try (Session session = driver.session()) {
            session.executeWriteWithoutResult(writeDisruption(disruption));
        }
    }

    public void createClosures(List<Disruption> disruptions) {
        try (Session session = driver.session()) {
            disruptions.forEach(disruption -> session.executeWriteWithoutResult(writeDisruption(disruption)));
        }
    }

    private static Consumer<TransactionContext> writeDisruption(Disruption disruption) {
        return transactionContext -> {
            Query q = CREATE_CLOSURE.withParameters(parameters(
                    "id", disruption.id,
                    "lat", disruption.centrum.getLatitude(),
                    "lon", disruption.centrum.getLongitude(),
                    "radius", disruption.radius,
                    "ttl", disruption.ttl,
                    "severity", disruption.severity,
                    "category", disruption.category,
                    "subcategory", disruption.subCategory,
                    "comment", disruption.comment,
                    "closed", disruption.closed,
                    "update", disruption.update,
                    "updateTime", disruption.updateTime.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            ));
            transactionContext.run(q);
        };
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
                d.severity = record.get("d").get("severity").asString();
                d.category = record.get("d").get("category").asString();
                d.subCategory = record.get("d").get("severity").asString();
                d.comment = record.get("d").get("comment").asString();
                d.update = record.get("d").get("update").asString();
                d.updateTime = java.sql.Timestamp.valueOf(record.get("d").get("updateTime").asLocalDateTime());
                d.closed = record.get("d").get("closed").asBoolean(false);
                disruptions.add(d);
            });
        }
        return disruptions;
    }
}
