package londonSafeTravel.client.gui;

import londonSafeTravel.client.DisruptionsRequest;
import londonSafeTravel.client.QueryPointRequest;
import londonSafeTravel.client.RoutingRequest;
import londonSafeTravel.schema.graph.Disruption;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainApp {
    private static final double MAX_LAT = 51.7314463;
    private static final double MIN_LAT = 51.2268448;
    private static final double MAX_LON = 0.399670;
    private static final double MIN_LON = -0.6125035;

    private JTextField textFieldSearch;
    private JButton buttonSearch;
    private JPanel rootPanel;
    private JXMapViewer mapViewer;
    private JButton buttonRefresh;
    private JCheckBox showDisruptionsCheckBox;
    private JRadioButton foot;
    private JRadioButton bicycle;
    private JRadioButton motorVehicles;

    private GlobalPainter globalPainter;

    public MainApp() {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Create painter
        globalPainter = new GlobalPainter();

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
        mapViewer.setOverlayPainter(globalPainter);

        mapViewer.addMouseListener(new MouseListener() {
            londonSafeTravel.schema.graph.Point start = null;
            londonSafeTravel.schema.graph.Point end = null;
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON3)
                    return;

                System.out.println("CLICK!");

                var coordinates = mapViewer.convertPointToGeoPosition(e.getPoint());
                if(coordinates.getLatitude() > MAX_LAT || coordinates.getLatitude() < MIN_LAT ||
                    coordinates.getLongitude() > MAX_LON || coordinates.getLongitude() < MIN_LON)
                {
                    System.out.println("Skipping click outside of bounds");
                    return;
                }

                String type="";
                if(motorVehicles.isSelected())
                    type = "car";
                else if(bicycle.isSelected())
                    type = "bicycle";
                else if(foot.isSelected())
                    type = "foot";

                QueryPointRequest request = null;
                try {
                    request = new QueryPointRequest(
                            "localhost:8080", coordinates.getLatitude(), coordinates.getLongitude(), type);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                if(start == null)
                    start = request.getPoint();
                else {
                    end = request.getPoint();
                    System.out.println("Routing begins!");
                    System.out.println(start);
                    System.out.println(end);

                    // Create a track from the geo-positions
                    try {
                        List<GeoPosition> track = new RoutingRequest(
                                "localhost:8080", start.getId(), end.getId()
                        ).getRouteGeo();

                        System.out.println("Routing completed " + track.size() + " hops!");

                        // Set the focus
                        mapViewer.zoomToBestFit(new HashSet<>(track), 0.7);

                        globalPainter.setRoute(track);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    start = null;
                    end = null;
                }
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
        });

        tileFactory.addTileListener(new TileListener() {

            @Override
            public void tileLoaded(Tile tile) {
                //if (tileFactory.getPendingTiles() == 0) {
                //    System.out.println("All tiles loaded!");
                //}

            }
        });
        mapViewer.setTileFactory(tileFactory);

        showDisruptionsCheckBox.addItemListener(new ItemListener() {
            MouseAdapter mouseListener;

            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() != ItemEvent.SELECTED) {
                    mapViewer.removeMouseListener(mouseListener);
                    mapViewer.removeMouseMotionListener(mouseListener);

                    globalPainter.removeDisruptions();
                    mapViewer.updateUI();

                    return;
                }

                ArrayList<Disruption> disruptions = null;
                try {
                    disruptions = new DisruptionsRequest(
                            "localhost:8080").getDisruptions();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                Set<DisruptionWaypoint> waypoints = disruptions
                        .stream()
                        .map(DisruptionWaypoint::new)
                        .collect(Collectors.toSet());

                globalPainter.setDisruptions(waypoints);
                mapViewer.updateUI();

                mouseListener = new MouseAdapter() {
                    private boolean isOnWaypoint(Point point, DisruptionWaypoint waypoint) {
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
                        for(var waypoint : waypoints) {
                            //check if near the mouse
                            if (!isOnWaypoint(me.getPoint(), waypoint))
                                continue;

                            var dialog = new DisruptionDialog(waypoint.getDisruption());
                            //@todo dimensions and default position!
                            dialog.setVisible(true);
                        }
                    }

                    @Override
                    public void mouseMoved(MouseEvent me) {
                        for(var waypoint : waypoints) {
                            if (!isOnWaypoint(me.getPoint(), waypoint))
                                continue;

                            mapViewer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            return;
                        }

                        mapViewer.setCursor(Cursor.getDefaultCursor());
                    }
                };

                mapViewer.addMouseListener(mouseListener);
                mapViewer.addMouseMotionListener(mouseListener);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainApp");
        frame.setContentPane(new MainApp().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
