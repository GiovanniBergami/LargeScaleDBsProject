package londonSafeTravel.OSMImporter.POI;

import londonSafeTravel.schema.Location;

import java.util.HashMap;

abstract class POI {
    public abstract Location getCentrum();

    public String name;
    public String className;
    public String type;
    public long osmID;

    public HashMap<String, String> osmTags;
}
