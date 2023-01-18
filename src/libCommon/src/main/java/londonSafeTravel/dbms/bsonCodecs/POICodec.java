package londonSafeTravel.dbms.bsonCodecs;

import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.Point;
import londonSafeTravel.schema.document.poi.PointOfInterest;
import londonSafeTravel.schema.document.poi.PointOfInterestOSM;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class POICodec implements Codec<PointOfInterest> {
    private final Codec<Document> documentCodec;
    private final Codec<Point> pointCodec;
    private final Codec<Geometry> geometryCodec;

    public POICodec(CodecRegistry registry) {
        this.documentCodec = registry.get(Document.class);
        this.pointCodec = registry.get(Point.class);
        this.geometryCodec = registry.get(Geometry.class);
    }
    @Override
    public PointOfInterest decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(reader, decoderContext);
        Point coordinates = null;
        Geometry perimeter = null;

        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String name = reader.readName();
            if(name.equals("coordinates"))
                coordinates = pointCodec.decode(reader, decoderContext);
            else if(name.equals("perimeter"))
                perimeter = pointCodec.decode(reader, decoderContext);
            else
                reader.skipValue();
        }
        reader.readEndDocument();

        String type = document.getString("type");

        PointOfInterest ret;

        if (type.equals("OSM-POI")) {
            var poi = new PointOfInterestOSM();

            poi.tags = new HashMap<>();
            for (Map.Entry<String, Object> entry : document.get("tags", Document.class).entrySet()) {
                poi.tags.put(entry.getKey(), (String) entry.getValue());
            }

            poi.perimeter = perimeter;

            ret = poi;
        } else
            ret = new PointOfInterest();

        ret.poiID = document.getString("poiID");
        ret.name = document.getString("name");
        ret.coordinates = coordinates;

        return ret;
    }

    @Override
    public void encode(BsonWriter writer, PointOfInterest value, EncoderContext encoderContext) {
        Document document = new Document();
        for (Field field : value.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(BsonProperty.class)) {
                field.setAccessible(true);
                try {
                    document.put(field.getName(), field.get(value));
                } catch (IllegalAccessException e) {
                    // handle exception
                }
            }
        }
        documentCodec.encode(writer, document, encoderContext);
    }

    @Override
    public Class<PointOfInterest> getEncoderClass() {
        return PointOfInterest.class;
    }
}
