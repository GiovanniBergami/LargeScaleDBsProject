package londonSafeTravel.schema.document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;
import londonSafeTravel.driver.tims.RoadDisruptionUpdate;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


import static com.mongodb.client.model.Filters.geoWithin;

public class PointOfInterestDAO {
    private ConnectionMongoDB connection = new ConnectionMongoDB();

    private MongoDatabase db = connection.giveDB();
    private MongoCollection collection = db.getCollection("PointOfInterest");

    public void printAll() {

        try (
                MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }

    }

    public static void main(String[] argv)
    {
        PointOfInterestDAO poiDAO = new PointOfInterestDAO();

        poiDAO.query1(-1, 100, -1, 90).forEach(d -> {
            System.out.println(d.toJson());
        });
    }


    public Collection<Document> query1(double minLong, double maxLong, double minLat, double maxLat)
    {
        Polygon region = new Polygon(Arrays.asList(
                new Position(minLong, minLat),
                new Position(maxLong, minLat),
                new Position(maxLong, maxLat),
                new Position(minLong, maxLat),
                new Position(minLong, minLat)
        ));

        Bson myMatch = geoWithin("coordinates", region);
        ArrayList<Document> results = new ArrayList<>();
        collection.find(myMatch)
                .forEach(doc -> {
                    results.add((Document) doc);
                });
        return results;
    }
}
