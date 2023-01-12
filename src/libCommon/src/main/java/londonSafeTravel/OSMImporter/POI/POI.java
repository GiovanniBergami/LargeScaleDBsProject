package londonSafeTravel.OSMImporter.POI;

import londonSafeTravel.schema.Location;

abstract class POI {
    public abstract Location getCentrum();

    public String name;
    public String className;
    public String type;
}
