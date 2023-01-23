package londonSafeTravel.schema.document;

import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.LineString;
import com.mongodb.client.model.geojson.Point;
import londonSafeTravel.schema.Location;
import org.bson.BsonElement;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Disruption {

    public static class Update{
        public Date start;
        public Date end;
        public String message;
    }
    public static class Street{
        public String name;
        public Boolean closure;
        public String direction;
        public List<LineString> segments;

        public Street() {segments = new ArrayList<>();}
    }
    @BsonProperty
    public String id;
    @BsonProperty
    public String type;
    @BsonProperty
    public Date start;
    @BsonProperty
    public Date end;
    @BsonProperty
    public Point coordinates;
    @BsonProperty
    public Geometry boundaries;

    @BsonProperty
    public String category;

    @BsonProperty
    public String subCategory;
    @BsonProperty
    public String severity;
    @BsonProperty
    public List<Update> updates;

    @BsonProperty
    public List<Street> streets;
    @BsonProperty("closure")
    public Boolean closure; // se stazione (0,1), se in street esiste una closure(0,1)

    public Disruption()
    {
        updates = new ArrayList<>();
        streets = new ArrayList<>();
    }
}
