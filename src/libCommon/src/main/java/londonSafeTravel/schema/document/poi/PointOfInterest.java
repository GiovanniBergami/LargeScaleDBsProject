package londonSafeTravel.schema.document.poi;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class PointOfInterest {
    // @TODO add enum
    //@BsonProperty("type")
    //public abstract String getType();
    @BsonProperty
    public String poiID;
    @BsonProperty
    public String name;
    @BsonProperty
    public Point coordinates;
}
