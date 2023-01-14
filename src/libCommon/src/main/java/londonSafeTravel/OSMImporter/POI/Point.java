package londonSafeTravel.OSMImporter.POI;

import londonSafeTravel.schema.Location;

class Point extends POI {
    private Location centrum;
    @Override
    public Location getCentrum() {
        return centrum;
    }
    public void setCentrum(Location c) {
        centrum = c;
    };
}
