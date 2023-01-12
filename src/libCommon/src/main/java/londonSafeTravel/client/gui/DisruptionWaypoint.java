package londonSafeTravel.client.gui;

import londonSafeTravel.schema.graph.Disruption;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.Objects;

public class DisruptionWaypoint extends DefaultWaypoint {
    private final Disruption disruption;

    public String getText() {
        return text;
    }

    private final String text;

    public DisruptionWaypoint(Disruption d) {
        super(
                new GeoPosition(
                        d.centrum.getLatitude(),
                        d.centrum.getLongitude())
        );
        this.disruption = d;
        this.text = d.id;
    }
    Disruption getDisruption() {
        return disruption;
    }

    /*public static class DisruptionWaypointOverlayPainter extends WaypointPainter<DisruptionWaypoint> {

        @Override
        protected void doPaint(Graphics2D g, JXMapViewer jxMapViewer, int width, int height) {
            for (var disruptionWaypoint : getWaypoints()) {
                Point2D point = jxMapViewer.getTileFactory().geoToPixel(
                        disruptionWaypoint.getPosition(), jxMapViewer.getZoom());
                Rectangle rectangle = jxMapViewer.getViewportBounds();

                int buttonX = (int)(point.getX() - rectangle.getX());
                int buttonY = (int)(point.getY() - rectangle.getY());

                JButton button = disruptionWaypoint.getButton();
                button.setLocation(buttonX - button.getWidth() / 2, buttonY - button.getHeight() / 2);
            }
        }
    }*/
}
