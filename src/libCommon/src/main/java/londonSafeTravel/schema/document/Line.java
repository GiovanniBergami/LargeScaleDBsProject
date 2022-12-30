package londonSafeTravel.schema.document;

import java.util.Collection;
import java.util.Date;

public class Line {
    public class Route extends londonSafeTravel.schema.document.Route{
        public Collection<String> stops;
        public String originationName;
        public String destinationName;
    }

    public String id;
    public Collection<Route> routes;
    public String name;

    public static class TerminatedClosureDisruption {
        public String id;

        public Date start;
        public Date end;
        // etc ... s
    }
    public Collection<TerminatedClosureDisruption> terminatedClosureDisruptions;
}
