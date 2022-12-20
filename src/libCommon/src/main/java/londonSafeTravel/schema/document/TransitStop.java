package londonSafeTravel.schema.document;

import java.util.Collection;
import java.util.Date;

public class TransitStop {
    public class Timetable{
        public Date arrival;
        public Date departure;
    }

    public class Route extends londonSafeTravel.schema.document.Route {
        public String line;
    }

    public class TerminatedDisruption{
        public String id;

        public Date start;
        public Date end;
        public String typeDisruption;
        // etc ... s
    }

    public String idPOI; // POI's id to refer to POI
    public String name;
    public Collection<Timetable> timeTables;
    public Collection<Route> routes;
    public Collection<TerminatedDisruption> terminatedDisruptions;
}
