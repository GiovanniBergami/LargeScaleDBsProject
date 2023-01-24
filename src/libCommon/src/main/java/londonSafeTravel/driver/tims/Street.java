package londonSafeTravel.driver.tims;

import com.github.filosganga.geogson.model.LineString;

import java.util.List;

class Street {
    public static class Segment {
        LineString lineString;
    }

    public String name;
    public String closure;
    public String directions;
    public List<Segment> segments;
}
