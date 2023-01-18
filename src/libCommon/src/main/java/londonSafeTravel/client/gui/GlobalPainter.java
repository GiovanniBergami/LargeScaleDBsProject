package londonSafeTravel.client.gui;

import londonSafeTravel.client.DisruptionsRequest;
import londonSafeTravel.client.POIRequest;
import londonSafeTravel.schema.Location;
import londonSafeTravel.schema.document.poi.PointOfInterest;
import londonSafeTravel.schema.graph.Disruption;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GlobalPainter extends AbstractPainter<JXMapViewer> {

    private final Color ROUTE_COLOR = Color.RED;
    private final boolean USE_ANTIALIASING = true;

    private List<GeoPosition> route;

    private final DefaultWaypointRenderer renderer =  new DefaultWaypointRenderer();
    private final POIRenderer poiRenderer=new POIRenderer(new File("assets/waypoints/poi.png"));
    private final Set<DisruptionWaypoint> disruptions = new HashSet<>();

    private final Set<POIWaypoint> pois = new HashSet<>();
    private final int MINIMUM_ZOOM_LEVEL = 4;

    private int oldZoom = -1;
    private double oldCenterX = -1;
    private double oldCenterY = -1;

    public GlobalPainter() throws IOException {
    }

    @Override
    public void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
        Rectangle viewportBounds = map.getViewportBounds();

        int newZoom = map.getZoom();
        double newCenterX = map.getCenter().getX();
        double newCenterY = map.getCenter().getY();
        if(newZoom != oldZoom || Math.abs(newCenterX - oldCenterX) > 200 || Math.abs(newCenterY - oldCenterY) > 200) {
            oldZoom = newZoom;
            oldCenterX = newCenterX;
            oldCenterY = newCenterY;
            // POI

            //long minLat, maxLat, minLon, maxLon;

            GeoPosition pointTopLeft;
            GeoPosition pointBottomRight;

            Point2D topLeft = map.getLocation();
            topLeft.setLocation(
                    topLeft.getX() + viewportBounds.getX(),
                    topLeft.getY() + viewportBounds.getY());

            Point2D bottomRight = map.getLocation();
            bottomRight.setLocation(
                    bottomRight.getX() + viewportBounds.getX() + viewportBounds.getWidth(),
                    bottomRight.getY() + viewportBounds.getY() + viewportBounds.getHeight());

            pointTopLeft = map.getTileFactory().pixelToGeo(topLeft,newZoom);
            pointBottomRight = map.getTileFactory().pixelToGeo(bottomRight,newZoom);

            System.out.println("PointTopLeft:"+pointTopLeft + "\tand pointBottomRight: "+ pointBottomRight);

            Location tl = new Location(pointTopLeft.getLatitude(), pointTopLeft.getLongitude());
            Location br = new Location(pointBottomRight.getLatitude(), pointBottomRight.getLongitude());
            System.out.println(tl.metricNorm(br) + "\t" + newZoom);
            if ( newZoom <= 3){
                ArrayList<PointOfInterest> pois = null;
                try {
                    pois = new POIRequest(
                            "localhost:8080",pointTopLeft.getLatitude(),pointTopLeft.getLongitude(),pointBottomRight.getLatitude(),pointBottomRight.getLongitude()).getPOIs();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                Set<POIWaypoint> poisSET = pois
                        .stream()
                        .map(POIWaypoint::new)
                        .collect(Collectors.toSet());

                setPOIs(poisSET);

            }
            else
                setPOIs(new HashSet<>());
        }

        g.translate(-viewportBounds.getX(), -viewportBounds.getY());

        if (USE_ANTIALIASING)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // do the drawing
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(4));

        drawRoute(g, map);

        // do the drawing again
        g.setColor(ROUTE_COLOR);
        g.setStroke(new BasicStroke(2));

        drawRoute(g, map);

        // Draw disruptions
        drawDisruptions(g, map);
        drawPOIs(g,map);

        g.translate(viewportBounds.getX(), viewportBounds.getY());
    }

    /**
     * @param g   the graphics object
     * @param map the map
     */
    private void drawRoute(Graphics2D g, JXMapViewer map) {
        if(this.route == null)
            return;

        int lastX = 0;
        int lastY = 0;

        boolean first = true;



        for (GeoPosition gp : route) {
            // convert geo-coordinate to world bitmap pixel
            Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
            if (first) {
                first = false;
            } else {
                g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
            }

            lastX = (int) pt.getX();
            lastY = (int) pt.getY();
        }
    }

    private void drawDisruptions(Graphics2D g, JXMapViewer map) {
        for (var w : disruptions) {
            renderer.paintWaypoint(g, map, w);
        }
    }

    private void drawPOIs(Graphics2D g, JXMapViewer map) {
        for (var w : pois) {
            poiRenderer.paintWaypoint(g, map, w);
        }
    }
    public void setRoute(List<GeoPosition> route) {
        this.route = route;
    }

    public void setDisruptions(Set<DisruptionWaypoint> disruptions) {
        this.disruptions.clear();
        this.disruptions.addAll(disruptions);
    }
    public void setPOIs(Set<POIWaypoint> pois){
        this.pois.clear();
        this.pois.addAll(pois);
    }


    public void removeDisruptions() {
        this.disruptions.clear();
    }
}
