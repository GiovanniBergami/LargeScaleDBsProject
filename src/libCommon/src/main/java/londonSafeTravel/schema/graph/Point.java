package londonSafeTravel.schema.graph;

import londonSafeTravel.schema.Location;

public class Point {
    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", location=" + location +
                '}';
    }

    private final long id;
    public Location location;

    public Point(long id, double latitude, double longitude){
        this.id=id;
        this.location=new Location(latitude,longitude);
    }

    public long getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }
}

