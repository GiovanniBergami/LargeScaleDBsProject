package londonSafeTravel.dbms.document;

import com.google.common.collect.Lists;
import com.mongodb.DBObjectCodecProvider;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.TextSearchOptions;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;
import com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider;
import londonSafeTravel.dbms.bsonCodecs.POICodec;
import londonSafeTravel.schema.GeoFactory;
import londonSafeTravel.schema.Location;
import londonSafeTravel.schema.document.poi.PointOfInterest;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
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

        System.out.println(poiDAO.getPOI("25507035").name);
    }

    public void insert(PointOfInterest poi) {
        collection.insertOne(poi);
    }

    public PointOfInterest getPOI(String id) {
        Bson match = eq("poiID", id);
        return collection.find(match).first();
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

    public Collection<PointOfInterest> findPlace(String name){

        ArrayList<PointOfInterest> results = new ArrayList<>();
        collection.find(
                Filters.regex("name", Pattern.compile(name, Pattern.CASE_INSENSITIVE))
        ).limit(20).forEach(results::add);

        return results;
    }
}
