package londonSafeTravel.dbms.document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;
import londonSafeTravel.schema.document.ConnectionMongoDB;
import londonSafeTravel.schema.document.poi.PointOfInterest;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


import static com.mongodb.client.model.Filters.geoWithin;

public class PointOfInterestDAO {
    protected ConnectionMongoDB connection;
    private final MongoCollection<PointOfInterest> collection;

    public PointOfInterestDAO(ConnectionMongoDB connection) {
        this.connection = connection;
        MongoDatabase db = connection.giveDB();
        this.collection = db.getCollection("PointOfInterest", PointOfInterest.class);
    }

    public void printAll() {
        try (
                MongoCursor<PointOfInterest> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next());
            }
        }
    }

    public static void main(String[] argv)
    {
        PointOfInterestDAO poiDAO = new PointOfInterestDAO(new ConnectionMongoDB());

        poiDAO.query1(-1, 100, -1, 90).forEach(d -> {
            System.out.println(d.name);
        });
    }

    public void insert(PointOfInterest poi) {
        collection.insertOne(poi);
    }


    public Collection<PointOfInterest> query1(double minLong, double maxLong, double minLat, double maxLat)
    {
        Polygon region = new Polygon(Arrays.asList(
                new Position(minLong, minLat),
                new Position(maxLong, minLat),
                new Position(maxLong, maxLat),
                new Position(minLong, maxLat),
                new Position(minLong, minLat)
        ));

        Bson myMatch = geoWithin("coordinates", region);
        ArrayList<PointOfInterest> results = new ArrayList<>();
        collection.find(myMatch).forEach(results::add);
        return results;
    }
}
