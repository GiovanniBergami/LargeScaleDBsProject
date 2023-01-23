package londonSafeTravel.schema.document.poi;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class PointOfInterest {
    public String getType() {
        return "GENERIC";
    };
    @BsonProperty
    public String poiID;
    @BsonProperty
    public String name;
    @BsonProperty
    public Point coordinates;
}
