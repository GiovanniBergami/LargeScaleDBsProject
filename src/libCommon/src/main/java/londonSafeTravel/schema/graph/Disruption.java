package londonSafeTravel.schema.graph;

import londonSafeTravel.schema.Location;

import java.util.Date;

public class Disruption {
    public Location centrum;
    public String id;
    public double radius;
    public String severity;
    public long ttl;

    public String category;
    public String subCategory;

    public String comment;

    public String update;
    public Date updateTime;
    public boolean closed;
}
