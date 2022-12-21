package londonSafeTravel.schema.graph;

import londonSafeTravel.schema.Location;

public class Point {
    public int id;
    public Location location;

    public Point(int id, double latitude, double longitude){
        this.id=id;
        this.location=new Location(latitude,longitude);
    }

    public int getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }
}

