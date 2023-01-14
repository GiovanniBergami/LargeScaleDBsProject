package londonSafeTravel.client.gui;

import londonSafeTravel.client.HeatmapRequest;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class AnalyticsMap {
    private JTable queryResult;
    private JPanel mainPanel;
    private JXMapViewer mapViewer;
    private JSlider partitionSize;

    public AnalyticsMap() throws Exception {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        HeatmapPainter heatmapPainter = new HeatmapPainter();

        // Use 3 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(3);

        mapViewer.setZoom(6);
        mapViewer.setAddressLocation(new GeoPosition(51.5067, -0.1269)); // London

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));
        //mapViewer.setOverlayPainter(globalPainter);
        mapViewer.setOverlayPainter(heatmapPainter);

        var heatmapReq = new HeatmapRequest("localhost:8080", "Works");

        heatmapPainter.setHeatmap(heatmapReq.heatmap());

    }

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("AnalyticsMap");
        frame.setContentPane(new AnalyticsMap().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
