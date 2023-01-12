package londonSafeTravel.OSMImporter.POI;

import londonSafeTravel.schema.Location;

import java.util.List;

class Way extends POI{

    @Override
    public Location getCentrum() {
        return new Location(
                perimeter.stream().mapToDouble(Location::getLatitude).sum() / perimeter.size(),
                perimeter.stream().mapToDouble(Location::getLongitude).sum() / perimeter.size()
        );
    }

    private List<Location> perimeter;

    public void setPerimeter(List<Location> perimeter) {
        this.perimeter = perimeter;
    }
}
