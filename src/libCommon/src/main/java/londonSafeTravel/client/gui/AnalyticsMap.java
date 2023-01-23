package londonSafeTravel.client.gui;

import londonSafeTravel.client.net.HeatmapRequest;
import londonSafeTravel.client.net.LineGraphRequest;
import londonSafeTravel.client.net.StatTableRequest;
import londonSafeTravel.schema.Location;
import org.bson.Document;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

public class AnalyticsMap {
    private final HeatmapPainter heatmapPainter;
    private final XYSeriesCollection dataset;
    private JTable queryResult;
    private JPanel mainPanel;
    private JXMapViewer mapViewer;
    private JSlider partitionSize;

    private String selectedClass = "Infrastructure Issue";

    private JComboBox<String> classDisruption;
    private JButton updateButton;
    private JPanel graphContainer;
    private void updateHeatmap(String selectedClass, int size) {
        try {
            long latitudeLength = 0;
            long longitudeLength = 0 ;
            if(size == 0){
                latitudeLength = 10;
                longitudeLength = 10;
            }
            else if(size == 1){
                latitudeLength = 25;
                longitudeLength = 25;
            }
            else{
                latitudeLength = 50;
                longitudeLength = 50;
            }
            var heatmapReq = new HeatmapRequest("localhost:8080", selectedClass, latitudeLength, longitudeLength);
            heatmapPainter.setHeatmap(heatmapReq.heatmap());
            heatmapPainter.setDimLat(latitudeLength);
            heatmapPainter.setDimLon(longitudeLength);
            mapViewer.repaint();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public void buildTable(){

        Rectangle viewportBounds = mapViewer.getViewportBounds();
        GeoPosition pointTopLeft;
        GeoPosition pointBottomRight;

        Point2D topLeft = mapViewer.getLocation();

        topLeft.setLocation(
                topLeft.getX() + viewportBounds.getX(),
                topLeft.getY() + viewportBounds.getY());

        Point2D bottomRight = mapViewer.getLocation();
        bottomRight.setLocation(
                bottomRight.getX() + viewportBounds.getX() + viewportBounds.getWidth(),
                bottomRight.getY() + viewportBounds.getY() + viewportBounds.getHeight());

        pointTopLeft = mapViewer.getTileFactory().pixelToGeo(topLeft,mapViewer.getZoom());
        pointBottomRight = mapViewer.getTileFactory().pixelToGeo(bottomRight,mapViewer.getZoom());

        System.out.println("PointTopLeft:"+pointTopLeft + "\tand pointBottomRight: "+ pointBottomRight);

        Location tl = new Location(pointTopLeft.getLatitude(), pointTopLeft.getLongitude());
        Location br = new Location(pointBottomRight.getLatitude(), pointBottomRight.getLongitude());


        Collection<Document> result = null;
        try {
            result = new StatTableRequest(
                    "localhost:8080",
                    pointTopLeft.getLatitude(),pointTopLeft.getLongitude(),
                    pointBottomRight.getLatitude(),pointBottomRight.getLongitude()
            ).getResults();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
         // to continue
        // Table for query
        DefaultTableModel tableData = (DefaultTableModel) queryResult.getModel();
        tableData.setRowCount(0);

        for(var row : result){
            Object[] tableRow = {row.getString("severity"), row.getString("type"), row.getInteger("count")};
            tableData.addRow(tableRow);
        }


    }
    public AnalyticsMap() throws Exception {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo("Humanitarian", "http://tile-c.openstreetmap.fr/hot");
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        heatmapPainter = new HeatmapPainter(50,50);

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
        classDisruption.addItem("Infrastructure Issue");
        classDisruption.addItem("Works");
        classDisruption.addItem("Hazard(s)");
        classDisruption.addItem("Traffic Incidents");
        classDisruption.addItem("Special and Planned Events");
        classDisruption.addItem("Traffic Volume");

        DefaultTableModel tableData = (DefaultTableModel) queryResult.getModel();
        tableData.addColumn("Severity");
        tableData.addColumn("Most common disruption");
        tableData.addColumn("Cardinality");
        queryResult.setAutoCreateRowSorter(true);

        dataset = new XYSeriesCollection();
        var chart = ChartFactory.createXYLineChart(
                "Average active disruptions by hour",
                "Hour",
                "Cardinality",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        NumberAxis domain = (NumberAxis) ((XYPlot)chart.getPlot()).getDomainAxis();
        domain.setRange(0.0, 23.0);
        domain.setTickUnit(new NumberTickUnit(1.0));

        graphContainer.add(new ChartPanel(chart));

        //{"Infrastructure Issue", "Works", "Hazard(s)", "Traffic Incidents", "Special and Planned Events", "Traffic Volume"};
        classDisruption.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    updateHeatmap((String)classDisruption.getSelectedItem(), partitionSize.getValue());
                    updateGraph((String)classDisruption.getSelectedItem());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        classDisruption.setSelectedItem("Works");

        partitionSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateHeatmap((String)classDisruption.getSelectedItem(), partitionSize.getValue());
            }
        });

        var heatmapReq = new HeatmapRequest("localhost:8080", "Infrastructure Issue", 50, 50);

        heatmapPainter.setHeatmap(heatmapReq.heatmap());

        // TABLE INSERT HERE
      //  buildTable();
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildTable();
            }
        });


    }

    private void updateGraph(String selectedItem) throws Exception {
        var request = new LineGraphRequest("localhost:8080", selectedItem);
        var series = new XYSeries("by hour");

        double[] counts = new double[24];
        request.getEntries().forEach(lineGraphEntry -> {
            counts[(int) lineGraphEntry.hour] = lineGraphEntry.count;
        });

        for(int i = 0; i < 24; i++) {
            series.add(i, counts[i]);
        }

        dataset.removeAllSeries();
        dataset.addSeries(series);
    }

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("AnalyticsMap");
        var analMap = new AnalyticsMap();
        frame.setContentPane(analMap.mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        analMap.updateButton.doClick();
    }
}
