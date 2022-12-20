package londonSafeTravel.driver.tims;

import java.util.Collection;
import java.util.Date;

public class RoadDistruptionUpdate {
    class Geometry
    {
        Collection<Collection<Long>> coordinates;
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

    long point;
    Geometry geometry;

    public static void main(String[] argv)
    {

    }
}