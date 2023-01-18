package londonSafeTravel.dbms.document;

import com.google.common.collect.Lists;
import com.mongodb.DBObjectCodecProvider;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;
import com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider;
import com.mongodb.client.model.geojson.codecs.GeometryCodec;
import londonSafeTravel.dbms.bsonCodecs.POICodec;
import londonSafeTravel.schema.document.poi.PointOfInterest;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static com.mongodb.client.model.Filters.geoWithin;

public class PointOfInterestDAO {
    protected ConnectionMongoDB connection;
    private final MongoCollection<PointOfInterest> collection;

    public PointOfInterestDAO(ConnectionMongoDB connection) {
        this.connection = connection;
        MongoDatabase db = connection.giveDB();

        CodecRegistry POIRegistry0 = CodecRegistries.fromRegistries(
                CodecRegistries.fromProviders(
                        // I have literally no idea but it's needed for GEOJson stuff
                        new GeoJsonCodecProvider(),
                        new ValueCodecProvider(),
                        new BsonValueCodecProvider(),
                        new DBObjectCodecProvider()
                ),
                db.getCodecRegistry()
        );

        CodecRegistry POIRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new POICodec(POIRegistry0)),
                POIRegistry0
        );

        this.collection = db.withCodecRegistry(POIRegistry).getCollection("PointOfInterest", PointOfInterest.class);
    }

    public void printAll() {
        try (
                MongoCursor<PointOfInterest> cursor = collection.find().iterator()
        ) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next());
            }
        }
    }

    public static void main(String[] argv)
    {
        PointOfInterestDAO poiDAO = new PointOfInterestDAO(new ConnectionMongoDB("mongodb://172.16.5.47:27017"));

        var res = poiDAO.selectPOIsInArea(0, 10, -90, 90);

        System.out.println(res.size());
    }

    public void insert(PointOfInterest poi) {
        collection.insertOne(poi);
    }


    public List<PointOfInterest> selectPOIsInArea(double minLong, double maxLong, double minLat, double maxLat)
    {
        assert (minLong < maxLong);
        assert (minLat < maxLat);

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
