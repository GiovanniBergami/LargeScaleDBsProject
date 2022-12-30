package londonSafeTravel.schema.document;

import londonSafeTravel.schema.Location;
import org.bson.BsonElement;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Disruption {

    public static class Update{
        public Date start;
        public Date end;
        public String message;
    }
    public static class Street{
        public String name;
        public Boolean closure;
    }
    @BsonProperty
    public String id;
    public enum Type{PUBLIC_TRANSPORT, ROAD}; // da rivedere
    @BsonProperty
    public String type;
    @BsonProperty
    public Date start;
    @BsonProperty
    public Date end;
    @BsonProperty
    public Location coordinates;
    @BsonProperty
    public Collection<Location> boundaries;

    @BsonProperty
    public String category;

    @BsonProperty
    public String subCategory;
    @BsonProperty
    public String severity;
    @BsonProperty
    public ArrayList<Update> updates;

    @BsonProperty
    public Collection<Street> streets;
    @BsonProperty("closure")
    public Boolean closure; // se stazione (0,1), se in street esiste una closure(0,1)

    public Disruption()
    {
        updates = new ArrayList<>();
    }
}
