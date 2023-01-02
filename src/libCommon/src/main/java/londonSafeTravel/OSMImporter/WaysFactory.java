package londonSafeTravel.OSMImporter;

import londonSafeTravel.schema.graph.Point;
import londonSafeTravel.schema.graph.Way;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class WaysFactory {
    private WaysFactory() {
    }

    public static final double CONVERT_KMH_MS = 0.277778;
    public static final double CONVERT_MPH_MS = 0.44704;

    public final static HashMap<String, Double> accessTags = new HashMap<>() {{
        put("foot", 4.5 * CONVERT_KMH_MS);
        put("bicycle", 18.0 * CONVERT_KMH_MS);
        put("vehicle", 112 * CONVERT_KMH_MS);
        put("motor_vehicle", 112 * CONVERT_KMH_MS);
        put("motorcycle", 112 * CONVERT_KMH_MS);
        put("motorcar", 112 * CONVERT_KMH_MS);
        //put("carriage", );
    }};

    private final static HashSet<String> noAccessValues = new HashSet<>() {{
        add("no");
        add("private");
        add("destination");
        add("use_sidepath");
    }};

    private final static HashSet<String> yesAccessValues = new HashSet<>() {{
        add("yes");
        add("designated");
        add("dismount");
    }};

    private final static HashMap<String, Double> nationalSpeedLimits = new HashMap<>() {{
        put("motorway", 70.0 * CONVERT_MPH_MS);
        put("residential", 20.0 * CONVERT_MPH_MS);
        put("service", 15.0 * CONVERT_MPH_MS);
    }};

    private final static HashMap<String, HashSet<String>> defaultRestrictions = new HashMap<>() {{
        put("motorway", new HashSet<>() {{
            add("foot");
            add("bicycle");
        }});
        put("pedestrian", new HashSet<>() {{
            add("motor_vehicle");
        }});
        put("bus_guideway", new HashSet<>() {{
            add("motor_vehicle");
            add("bicycle");
        }});
        put("busway", new HashSet<>() {{
            add("motor_vehicle");
            add("bicycle");
        }});
        put("footway", new HashSet<>() {{
            add("motor_vehicle");
            add("bicycle");
        }});
        put("bridleway", new HashSet<>() {{
            add("motor_vehicle"); /*add("bicycle");*/
        }});
        put("steps", new HashSet<>() {{
            add("motor_vehicle");
            add("bicycle");
        }});
        put("corridor", new HashSet<>() {{
            add("motor_vehicle");
            add("bicycle");
        }});
        put("path", new HashSet<>() {{
            add("motor_vehicle");
        }});
        put("cycleway", new HashSet<>() {{
            add("motor_vehicle");
        }});
    }};

    private static final String[] ONEWAY_TAGS = {"oneway:bicycle", "oneway:motor_vehicle"};

    public static Way getWay(HashMap<String, String> tags, Point p1, Point p2, boolean forward) {
        Way w = new Way();
        //w.id = Long.parseLong(tags.get("id"));
        w.p1 = p1;
        w.p2 = p2;
        w.name = Objects.requireNonNullElse(tags.get("name"), "noName");
        w.roadClass = tags.get("highway");

        // Compute speed limit
        var speedLimitString = tags.get("maxspeed");
        double speedLimit = 0;
        if (speedLimitString == null) {
            var candidate = nationalSpeedLimits.get(w.roadClass);
            speedLimit = Objects.requireNonNullElse(candidate, 13.4112);
        } else {
            if (speedLimitString.contains("mph"))
                speedLimit = Double.parseDouble(speedLimitString.replace("mph", "")) * CONVERT_MPH_MS;
            else if (speedLimitString.contains("walk"))
                speedLimit = 6.0 * CONVERT_KMH_MS;
            else // assuming km/h :P
                try {
                    speedLimit = Double.parseDouble(speedLimitString) * CONVERT_KMH_MS;
                } catch (NumberFormatException f) {
                    // Assuming 30mph
                    System.err.println("I dodn't know this speedlimit " + f.getMessage() + " near node " + p1.getId());
                    speedLimit = 30 * CONVERT_MPH_MS;
                }
        }
        w.maxSpeed = speedLimit;

        double finalSpeedLimit = speedLimit;
        w.crossTimes = new HashMap<>();
        accessTags.forEach((mode, maxspeed) -> {
            // No access
            if (
                    // If the road has a specific NO ACCESS for this kind of mode
                    (tags.containsKey(mode) && noAccessValues.contains(tags.get(mode))) ||
                            // OR If the road has a default access for this mode to NO and the segment doesn't have
                            // a specific restriction
                            (!yesAccessValues.contains(mode) && defaultRestrictions.containsKey(w.roadClass) &&
                                    defaultRestrictions.get(w.roadClass).contains(mode))) {
                w.crossTimes.put(mode, Double.POSITIVE_INFINITY);
                return;
            }

            w.crossTimes.put(mode, Double.max(
                    p1.location.metricNorm(p2.location) / maxspeed,
                    p1.location.metricNorm(p2.location) / finalSpeedLimit
            ));
        });

        // NORMALIZE ONEWAY IS SUB-CLASSES
        if(tags.containsKey("oneway"))
            for(var onewayType : ONEWAY_TAGS)
                if(!tags.containsKey(onewayType))
                    tags.put(onewayType, tags.get("oneway"));

        accessTags.forEach((mode, maxspeed) -> {
            if(mode.equals("foot"))
                return;

            // Is it one-way?
            var oneway = tags.get("oneway:" + mode);
            if(oneway == null)
                return;

            if((oneway.equals("yes") || oneway.equals("1")) && !forward)
                w.crossTimes.put(mode, Double.POSITIVE_INFINITY);
            // Legacy oneway
            else if(oneway.equals("-1") && forward)
                w.crossTimes.put(mode, Double.POSITIVE_INFINITY);
            //else if(!oneway.equals("no"))
            //    System.err.println("I don't know oneway:" + mode + "=" + oneway + " near node " + p1.getId());
        });

        // ????
        AtomicReference<Boolean> allInfinity = new AtomicReference<>(true);
        accessTags.forEach((s, aDouble) -> {
            if (aDouble != Double.POSITIVE_INFINITY)
                allInfinity.set(false);
        });

        // We skip a way with no access
        if (allInfinity.get())
            return null;

        return w;
    }

    public static void main(String argv[]) {
        System.out.println(Double.parseDouble("30 mph".replace("mph", "")));
    }
}
