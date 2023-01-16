package londonSafeTravel.schema.graph;

public class RoutingHop {
    public Point point;
    public double time;

    public RoutingHop(Point point, double time) {
        this.point = point;
        this.time = time;
    }
}
