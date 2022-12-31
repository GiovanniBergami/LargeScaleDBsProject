package londonSafeTravel.schema.graph;

import java.util.HashMap;

public class Way {
    public Point p1;
    public Point p2;
    public String name;
    public double maxSpeed;
    public String roadClass;
    public HashMap<String, Double> crossTimes;
}
