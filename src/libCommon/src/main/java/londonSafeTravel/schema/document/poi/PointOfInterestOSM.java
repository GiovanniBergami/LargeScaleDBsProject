package londonSafeTravel.schema.document.poi;

import com.mongodb.client.model.geojson.Geometry;

import java.util.HashMap;

public class PointOfInterestOSM extends PointOfInterest{
    public HashMap<String, String> tags;
    public Geometry perimeter;

    @Override
    public String getType() {
        return "OSM-POI";
    }
}
