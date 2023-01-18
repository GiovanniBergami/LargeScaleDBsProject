package londonSafeTravel.client.gui;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

public class POIRenderer implements WaypointRenderer<Waypoint>
{
    private BufferedImage img = null;


    public POIRenderer(File imageFile) throws IOException {
        img = ImageIO.read(imageFile);
    }

    @Override
    public void paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint w)
    {
        if (img == null)
            return;

        Point2D point = map.getTileFactory().geoToPixel(w.getPosition(), map.getZoom());

        int x = (int)point.getX() -img.getWidth() / 2;
        int y = (int)point.getY() -img.getHeight();

        g.drawImage(img, x, y, null);
    }
}

