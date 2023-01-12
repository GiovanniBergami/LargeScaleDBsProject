package londonSafeTravel.client.gui;

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
    private final JButton button;

    public String getText() {
        return text;
    }

    private final String text;

    public DisruptionWaypoint(String text, GeoPosition coord) {
        super(coord);
        this.text = text;

        button = new JButton(text.substring(0, 1));
        button.setSize(24, 24);
        button.setPreferredSize(new Dimension(24, 24));
        button.addMouseListener(new ShowDisruptionEventListener());
        button.setVisible(true);

        button.setIcon(new ImageIcon(
                Objects.requireNonNull(DefaultWaypointRenderer.class.getResource("/images/standard_waypoint.png")
                )));
    }
    JButton getButton() {
        return button;
    }

    private class ShowDisruptionEventListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            JOptionPane.showMessageDialog(button, "You clicked on " + text);
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
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
