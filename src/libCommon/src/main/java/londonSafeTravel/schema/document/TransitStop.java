package londonSafeTravel.schema.document;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public static class TerminatedDisruption{
        public String id;

        public LocalDateTime start;
        public LocalDateTime end;
        public String typeDisruption;

        public String category;
        // etc ... s
    }

    public String idPOI; // POI's id to refer to POI
    public String name;
    public Collection<Timetable> timeTables; //@fix me
    public Collection<Route> routes;
    public Collection<TerminatedDisruption> terminatedDisruptions;
    public static void main(String[] argv) {

        LocalDate testino = LocalDate.now();
        System.out.println(testino.getDayOfWeek());


    }

}
