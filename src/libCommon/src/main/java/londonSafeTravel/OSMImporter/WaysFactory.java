package londonSafeTravel.OSMImporter;

import londonSafeTravel.schema.graph.Point;
import londonSafeTravel.schema.graph.Way;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class WaysFactory {
    private WaysFactory() {}

    public static final double CONVERT_KMH_MS = 0.277778;
    public static final double CONVERT_MPH_MS = 0.44704;

    public final static HashMap<String, Double> accessTags = new HashMap<>(){{
        put("foot", 4.5 * CONVERT_KMH_MS); put("bicycle", 18.0 * CONVERT_KMH_MS); put("vehicle", 112 * CONVERT_KMH_MS);
        put("motor_vehicle", 112 * CONVERT_KMH_MS); put("motorcycle", 112 * CONVERT_KMH_MS); put("motorcar", 112 * CONVERT_KMH_MS);
        //put("carriage", );
    }};

    private final static HashSet<String> noAccessValues = new HashSet<>(){{
        add("no"); add("private"); add("destination");
    }};

    private final static HashMap<String, Double> nationalSpeedLimits = new HashMap<>(){{
        put("motorway", 70.0 * CONVERT_MPH_MS); put("residential", 20.0 * CONVERT_MPH_MS); put("service", 15.0 * CONVERT_MPH_MS);
    }};

    public static Way getWay(HashMap<String, String> tags, Point p1, Point p2)
    {
        Way w = new Way();
        w.p1 = p1;
        w.p2 = p2;
        w.name = Objects.requireNonNullElse(tags.get("name"), "");
        w.roadClass = tags.get("highway");

        // Compute speed limit
        var speedLimitString = tags.get("maxspeed");
        double speedLimit = 0;
        if(speedLimitString == null)
        {
            var candidate = nationalSpeedLimits.get(w.roadClass);
            speedLimit = Objects.requireNonNullElse(candidate, 13.4112);
        }
        else
        {
            if(speedLimitString.contains("mph"))
                speedLimit = Double.parseDouble(speedLimitString.replace("mph", "")) * CONVERT_MPH_MS;
            else // assuming km/h :P
                speedLimit = Double.parseDouble(speedLimitString) * CONVERT_KMH_MS;
        }
        w.maxSpeed = speedLimit;

        double finalSpeedLimit = speedLimit;
        w.crossTimes = new HashMap<>();
        accessTags.forEach((mode, maxspeed) -> {
            if(tags.containsKey(mode) && noAccessValues.contains(tags.get(mode)))
            {
                w.crossTimes.put(mode, Double.POSITIVE_INFINITY);
                return;
            }

            w.crossTimes.put(mode, Double.max(
                    p1.location.metricNorm(p2.location) / maxspeed,
                    p1.location.metricNorm(p2.location) / finalSpeedLimit
            ));
        });

        return w;
    }

    public static void main (String argv[])
    {
        System.out.println(Double.parseDouble("30 mph".replace("mph", "")));
    }
}
