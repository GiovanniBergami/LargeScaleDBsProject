package londonSafeTravel.schema.document;

import londonSafeTravel.schema.Location;

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
    public String id;
    public enum Type{PUBLIC_TRANSPORT, ROAD}; // da rivedere
    public String type;
    public Date start;
    public Date end;
    public Location coordinates;
    public Collection<Location> boundaries;

    public String category;

    public String subCategory;
    public String severity;
    public ArrayList<Update> updates;

    public Collection<Street> streets;
    public Boolean closure; // se stazione (0,1), se in street esiste una closure(0,1)

    public Disruption()
    {
        updates = new ArrayList<>();
    }
}
