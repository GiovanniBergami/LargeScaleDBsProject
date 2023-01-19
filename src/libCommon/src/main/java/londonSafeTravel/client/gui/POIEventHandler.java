package londonSafeTravel.client.gui;

import londonSafeTravel.schema.document.poi.PointOfInterest;
import org.jxmapviewer.JXMapViewer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class POIEventHandler extends MouseAdapter {
    List<PointOfInterest> pois;
    JXMapViewer mapViewer;

    public POIEventHandler(JXMapViewer mapViewer) {
        this.mapViewer = mapViewer;
    }

    public void setPois(List<PointOfInterest> pois) {
        this.pois = pois;
    }

    private boolean isOnWaypoint(Point point, POIWaypoint waypoint) {
        var gp_pt = mapViewer.getTileFactory().geoToPixel(
                waypoint.getPosition(), mapViewer.getZoom()
        );

        //convert to screen
        Rectangle rect = mapViewer.getViewportBounds();
        Point converted_gp_pt = new Point(
                (int) gp_pt.getX() - rect.x - 5,
                (int) gp_pt.getY() - rect.y - 35);

        // hitbox
        Rectangle hitbox = new Rectangle(converted_gp_pt, new Dimension(10, 40));

        return hitbox.contains(point);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if(pois == null)
            return;

        for (var singlePoi : pois) {
            //check if near the mouse
            POIWaypoint temp = new POIWaypoint(singlePoi);
            if (!isOnWaypoint(me.getPoint(), temp))
                continue;

            var dialog = new POIDialog(temp.getPoi());
            //@todo dimensions and default position!
            dialog.setVisible(true);
            return;
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        if(pois == null)
            return;

        for (var singlePoi : pois) {
            POIWaypoint temp = new POIWaypoint(singlePoi);
            if (!isOnWaypoint(me.getPoint(), temp))
                continue;

            mapViewer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        mapViewer.setCursor(Cursor.getDefaultCursor());
    }

};
