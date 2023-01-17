package londonSafeTravel.client.gui;

import londonSafeTravel.schema.document.poi.PointOfInterest;
import londonSafeTravel.schema.graph.Disruption;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class POIWaypoint extends DefaultWaypoint {
    private final PointOfInterest poi;

    public String getText() {
        return text;
    }

    private final String text;

    public POIWaypoint(PointOfInterest p) {
        super(
                new GeoPosition(
                        p.coordinates.getCoordinates().getValues().get(1),
                        p.coordinates.getCoordinates().getValues().get(0))
        );
        this.poi = p;
        this.text =  p.name;
    }

    public PointOfInterest getPoi() {
        return poi;
    }
}
