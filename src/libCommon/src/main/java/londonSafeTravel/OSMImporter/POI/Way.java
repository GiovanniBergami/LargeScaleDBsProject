package londonSafeTravel.OSMImporter.POI;

import londonSafeTravel.schema.Location;

import java.util.ArrayList;
import java.util.List;

class Way extends POI{

    @Override
    public Location getCentrum() {
        return new Location(
                perimeter.stream().mapToDouble(Location::getLatitude).sum() / perimeter.size(),
                perimeter.stream().mapToDouble(Location::getLongitude).sum() / perimeter.size()
        );
    }

    private final List<Location> perimeter = new ArrayList<>();

    public void addPerimeterPoint(Location point) {
        this.perimeter.add(point);
    }

    public void setPerimeter(List<Location> perimeter) {
        this.perimeter.clear();
        this.perimeter.addAll(perimeter);
    }
}
