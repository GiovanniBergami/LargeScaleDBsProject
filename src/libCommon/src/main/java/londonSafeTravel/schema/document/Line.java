package londonSafeTravel.schema.document;

import java.util.Collection;

public class Line {
    public class Route extends londonSafeTravel.schema.document.Route{
        public Collection<String> stops;
        public String originationName;
        public String destinationName;
    }

    public String id;
    public Collection<Route> routes;
    public String name;
}
