package londonSafeTravel.client.gui;

import londonSafeTravel.schema.document.HeatmapComputation;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeatmapPainter extends AbstractPainter<JXMapViewer> {
    private static class HeatmapKey {
        public long latitude; public long longitude;

        public HeatmapKey(long latitude, long longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public HeatmapKey(double latitude, double longitude) {
            this.latitude = ((long) (latitude * 1000)) ;
            this.longitude = (((long) (longitude * 1000)) / 5) * 5;
        }

        @Override
        public String toString() {
            return "<" +
                   "latitude=" + latitude +
                   ", longitude=" + longitude +
                   '>';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HeatmapKey that = (HeatmapKey) o;

            if (latitude != that.latitude) return false;
            return longitude == that.longitude;
        }

        @Override
        public int hashCode() {
            return (int)latitude + (int)(longitude >> 16);
        }
    }
    private static final long MAX_LAT = 51700;
    private static final long MIN_LAT = 51300;
    private static final long MAX_LON = 350;
    private static final long MIN_LON = -650;

    private static Color percentageToColor(double percentage) {
        if (percentage > 1) {
            percentage = 1;
        }
        else if (percentage < 0) {
            percentage = 0;
        }
        int red = (int)(255.0 * (percentage));
        int green = (int)(255.0 * (1 - percentage));
        int blue = 0;
        return new Color(red, green, blue, 255/2);
    }

    private final Map<HeatmapKey, HeatmapComputation> heatmap = new HashMap<>();

    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int i, int i1) {
        Rectangle viewportBounds = map.getViewportBounds();
        g.translate(-viewportBounds.getX(), -viewportBounds.getY());

        for(long latitude = MIN_LAT; latitude <= MAX_LAT; latitude += 1)
            for(long longitude = MIN_LON; longitude <= MAX_LON; longitude += 5){
                final double denominator = 3;
                double numerator = 0;

                var cell = heatmap.get(new HeatmapKey(latitude, longitude));
                if(cell != null)
                    numerator = cell.count;

                double percentage = numerator / denominator;
                var col = percentageToColor(percentage);
                System.out.println(latitude + "\t" + longitude + "\t" + percentage);

                g.setBackground(col);
                g.setColor(col);
                var test = map.getTileFactory().geoToPixel(
                        new GeoPosition(latitude / 1000.0, longitude / 1000.0),
                        map.getZoom());
                var test2 = map.getTileFactory().geoToPixel(
                        new GeoPosition(latitude / 1000.0 + 0.001, longitude / 1000.0 + 0.005),
                        map.getZoom());
                g.fillRect(
                        (int) test.getX(), (int) test.getY(),
                        (int) Math.abs(test2.getX() - test.getX()), (int) Math.abs(test2.getY() - test.getY())
                );

            }


        g.translate(viewportBounds.getX(), viewportBounds.getY());
    }

    public void setHeatmap(List<HeatmapComputation> h) {
        heatmap.clear();

        h.forEach(cell -> {
            var key = new HeatmapKey(cell.latitude, cell.longitude);
            if(cell.count >= 8)
                System.out.println(key + "\t" + cell.count);

            heatmap.put(key, cell);
        });
    }
}
