package londonSafeTravel.driver.tims;

import java.util.Collection;
import java.util.Date;

import londonSafeTravel.schema.Location;

public class RoadDistruptionUpdate {
    static class Geometry
    {
        Collection<Collection<Location>> coordinates;
    }

    String id;
    String category;
    String subCategory;
    String comments;
    String currentUpdate;
    Date currentUpdateDateTime;
    Date startDateTime;
    Date endDateTime;
    Date lastModifiedTime;
    String levelOfInterest;
    String status;

    Location point;
    Geometry geometry;

    public static void main(String[] argv)
    {

    }
}