package londonSafeTravel.client.gui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalPainter extends AbstractPainter<JXMapViewer> {

    private final Color ROUTE_COLOR = Color.RED;
    private final boolean USE_ANTIALIASING = true;

    private List<GeoPosition> route;

    private final DefaultWaypointRenderer renderer =  new DefaultWaypointRenderer();
    private final Set<DisruptionWaypoint> disruptions = new HashSet<>();
    @Override
    public void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
        Rectangle viewportBounds = map.getViewportBounds();

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

    public void setRoute(List<GeoPosition> route) {
        this.route = route;
    }

    public void setDisruptions(Set<DisruptionWaypoint> disruptions) {
        this.disruptions.clear();
        this.disruptions.addAll(disruptions);
    }
}
