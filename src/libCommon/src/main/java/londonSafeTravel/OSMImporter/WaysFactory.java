package londonSafeTravel.OSMImporter;

import java.util.HashSet;

public class WaysFactory {
    private WaysFactory() {}

    private final static HashSet<String> accessTags = new HashSet<>(){{
        add("foot"); add("bicycle"); add("vehicle"); add("motor_vehicle"); add("motorcycle"); add("motorcar");
        add("carriage");
    }};
}
