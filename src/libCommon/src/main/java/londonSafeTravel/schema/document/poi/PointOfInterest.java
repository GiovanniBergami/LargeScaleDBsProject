package londonSafeTravel.schema.document.poi;

import com.mongodb.client.model.geojson.Point;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class PointOfInterest {
    // @TODO add enum
    @BsonProperty
    public String type;
    @BsonProperty
    public String poi_id;
    @BsonProperty
    public String name;
    @BsonProperty
    public Point coordinates;
}
