package londonSafeTravel.dbms.document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.geoWithin;
import static com.mongodb.client.model.Projections.*;


public class DisruptionStatsDAO {
    private final MongoCollection<Document> collection;

    public DisruptionStatsDAO() {
        this(new ConnectionMongoDB());
    }

    public DisruptionStatsDAO(ConnectionMongoDB connection) {
        MongoDatabase db = connection.giveDB();
        this.collection = db.getCollection("Disruption");
    }

    /*
        This function will return a list witch contains the most common disruption in order to severity
    */
    public Collection<Document> commonDisruptionInArea(
            double minLong, double maxLong, double minLat, double maxLat) {
        Polygon region = new Polygon(Arrays.asList(
                new Position(minLong, minLat),
                new Position(maxLong, minLat),
                new Position(maxLong, maxLat),
                new Position(minLong, maxLat),
                new Position(minLong, minLat)
        ));

        Bson inSquare = geoWithin("coordinates", region);


        // Create the group stage
        Bson groupStage = Aggregates.group(
                new Document("severity", "$severity").append("category", "$category"),
                Accumulators.sum("count", 1)
        );
        Bson groupStage2 = Aggregates.group(
                "$_id.severity",
                Accumulators.max("count", "$count"),
                Accumulators.first("type", "$_id.category"),
                Accumulators.first("severity", "$_id.severity")
        );

        // Create the sort stage
        Bson sortStage = Aggregates.sort(Sorts.descending("count"));

        // Projèct

        Bson project = project(fields(excludeId(), include("severity",
                "type", "count")));

        // Combine the stages into a pipeline
        List<Bson> pipeline = Arrays.asList(Aggregates.match(inSquare),
                groupStage, groupStage2, sortStage, project);

        // Execute the aggregation
        Collection<Document> result = collection
                .aggregate(pipeline).into(new ArrayList<>());

        return result;
    }

    /*
    Build a heatmap of a certain class of disruption
     */

    public Collection<Document> queryHeatmap(
            double lenLat,
            double lenLong,
            String classDisruption)
    {
        Bson match = match(eq("category", classDisruption));
        Bson computeBuckets = new Document("$project", new Document()
                .append("latB", new Document("$multiply", Arrays.asList(
                        new Document("$floor", new Document("$divide", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList(
                                    "$coordinates.coordinates",
                                    1
                            )),
                            lenLat))),
                        lenLat
                )))
                .append("lngB", new Document("$multiply", Arrays.asList(
                        new Document("$floor", new Document("$divide", Arrays.asList(
                            new Document("$arrayElemAt", Arrays.asList(
                                    "$coordinates.coordinates",
                                    0
                            )),
                            lenLong))),
                        lenLong
                ))));
        Bson groupStage = Aggregates.group(
                new Document("latB", "$latB").append("lngB", "$lngB"),
                Accumulators.sum("count", 1)
        );

        Bson project = project(fields(
                excludeId(),
                include("count"),
                computed("latitude", "$_id.latB"),
                computed("longitude", "$_id.lngB")
        ));

        // Create the pipeline
        List<Bson> pipeline = Arrays.asList(
                match, computeBuckets, groupStage, project
        );
        //pipeline.forEach(bson -> System.out.println(bson.toBsonDocument()));


        // Execute the aggregation
        return collection.aggregate(pipeline).map(document -> {
            var lat = document.getDouble("latitude");
            var lon = document.getDouble("longitude");

            return document;
        }).into(new ArrayList<>());
    }
}
