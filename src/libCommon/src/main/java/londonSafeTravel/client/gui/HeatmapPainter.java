package londonSafeTravel.client.gui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;

public class HeatmapPainter extends AbstractPainter<JXMapViewer> {
    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int i, int i1) {
        var col = new Color(255, 0, 0, 126);
        g.setBackground(col);
        g.setColor(col);
        var test = map.getTileFactory().geoToPixel(new GeoPosition(51.5048489, -0.126149), map.getZoom());
        var test2 = map.getTileFactory().geoToPixel(new GeoPosition(51.5048489 + 0.05, -0.126149 + 0.05), map.getZoom());
        g.fillRect(
                (int) test.getX(), (int) test.getY(), (int) Math.abs(test2.getX() - test.getX()), (int) Math.abs(test2.getY() - test.getY())
        );
    }
}
