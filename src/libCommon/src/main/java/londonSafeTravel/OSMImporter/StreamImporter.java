package londonSafeTravel.OSMImporter;


import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import londonSafeTravel.schema.graph.Point;
import londonSafeTravel.schema.graph.Way;

public class StreamImporter {

    private final static HashSet<String> highwayTarget = new HashSet<>(){{
        add("motorway");
        add("trunk");
        add("primary");
        add("secondary");
        add("tertiary");
        add("unclassified");
        add("residential");
        add("motorway_link");
        add("trunk_link");
        add("primary_link");
        add("secondary_link");
        add("tertiary_link");
        add("living_street");
        add("service");
        add("pedestrian");
        add("track");
        add("bus_guideway");
        add("escape");
        add("raceway");
        add("road");
        add("busway");
        add("footway");
        add("bridleway");
        add("steps");
        add("corridor");
        add("path");
        add("cycleway");
    }};

    private static final String filenameDefault = "examples/greater-london-latest.osm";

    public static void main(String[] argv) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        final String filename = argv.length == 0 ? filenameDefault : argv[0];

        // Flusso per prima passata: i nodi
        XMLStreamReader r = factory.createXMLStreamReader(new FileReader(filename));

        // Tutti i nodi qua
        HashMap<Long, Point> map = new HashMap<Long, Point>();

        for (r.next(); r.hasNext(); r.next()) {
            if (!r.isStartElement())
                continue;

            if (!r.hasName() || !Objects.equals(r.getLocalName(), "node"))
                continue;

            final String currentNamespace = r.getNamespaceURI();
            double lat = Double.parseDouble(r.getAttributeValue(currentNamespace, "lat"));
            double lon = Double.parseDouble(r.getAttributeValue(currentNamespace, "lon"));
            long id = Long.parseLong(r.getAttributeValue(currentNamespace, "id"));
            Point p = new Point(id, lat, lon);

            map.put(id, p);
        }

        // Flusso per seconda passata: le vie
        r = factory.createXMLStreamReader(new FileReader(filename));

        //ManageWay manageWay = new ManageWay("neo4j://localhost:7687", "neo4j", "pass");

        ArrayList<Way> ways = new ArrayList<>();
        SortedMap<Long, Point> targetNodes = new TreeMap<>();
        long total = 0, totalWays = 0;
        final long cardinalityOverStime = 513192L;
        for (r.next(); r.hasNext(); r.next()) {
            if (!r.hasName())
                continue;

            // È via?
            final String currentNamespace = r.getNamespaceURI();
            if (!Objects.equals(r.getLocalName(), "way"))
                continue;

            final long wayID = Long.parseLong(r.getAttributeValue(currentNamespace, "id"));

            ArrayList<Long> locations = new ArrayList<>();
            HashMap<String, String> tags = new HashMap<>();
            for (r.next(); !(r.isEndElement() && Objects.equals(r.getLocalName(), "way")); r.next()) {
                if (!r.isStartElement() || !r.hasName())
                    continue;

                final String currentNamespaceIn = r.getNamespaceURI();
                // What kind of element are we parsing rn?
                if (Objects.equals(r.getLocalName(), "nd")) {
                    // It's a node
                    long id = Long.parseLong(r.getAttributeValue(currentNamespaceIn, "ref"));
                    locations.add(id);
                    targetNodes.put(id, map.get(id));
                } else if (Objects.equals(r.getLocalName(), "tag")) {
                    // It's a tag
                    tags.put(r.getAttributeValue(currentNamespaceIn, "k"), r.getAttributeValue(currentNamespaceIn, "v"));
                }
            }
            //System.out.println(locations.size()+" "+tags.get("highway")+" "+tags.get("maxspeed"));
            if (!tags.containsKey("highway") || !highwayTarget.contains(tags.get("highway")))
                continue;

            for (int i = 1; i < locations.size(); i++) {
                Point p1 = map.get(locations.get(i - 1));
                Point p2 = map.get(locations.get(i));

                Way w = WaysFactory.getWay(tags, p1, p2, true);
                Way w2 = WaysFactory.getWay(tags, p2, p1, false);

                if(w != null)
                {
                    w.id = wayID;
                    ways.add(w);
                }

                if(w2 != null)
                {
                    w2.id = wayID;
                    ways.add(w2);
                }
            }
        }

        // Writing nodes
        try(var nodesCSVWriter = new PrintWriter("examples/nodes.csv")) {
            nodesCSVWriter.println(convertToCSV(new String[]{
                    "id:ID", "latitude:double", "longitude:double", "coord:point{crs:WGS-84}", ":LABEL"}));
            targetNodes.forEach((id, point) -> {
                nodesCSVWriter.print(id);
                nodesCSVWriter.print(",");
                nodesCSVWriter.print(point.getLocation().getLatitude());
                nodesCSVWriter.print(",");
                nodesCSVWriter.print(point.getLocation().getLongitude());
                nodesCSVWriter.print(",\"{latitude:");
                nodesCSVWriter.print(point.getLocation().getLatitude());
                nodesCSVWriter.print(", longitude:");
                nodesCSVWriter.print(point.getLocation().getLongitude());
                nodesCSVWriter.print("}\",\"Point\"");
                nodesCSVWriter.println();
            });
        }

        // Writing edges
        try(var waysCSVWriter = new PrintWriter("examples/ways.csv")) {
            waysCSVWriter.println(convertToCSV(new String[]
                    {"p1:START_ID", "p2:END_ID", "id:long" ,"name", "class", "maxspeed:double",
                            "crossTimeFoot:double", "crossTimeBicycle:double", "crossTimeMotorVehicle:double", ":TYPE"}
            ));
            ways.forEach(way -> {
                waysCSVWriter.println(convertToCSV(new String[]{
                        Long.toString(way.p1.getId()),
                        Long.toString(way.p2.getId()),
                        Long.toString(way.id),
                        Objects.requireNonNullElse(way.name, "NoName"),
                        way.roadClass,
                        Double.toString(way.maxSpeed),
                        Double.toString(way.crossTimes.get("foot")),
                        Double.toString(way.crossTimes.get("bicycle")),
                        Double.toString(way.crossTimes.get("motor_vehicle")),
                        "CONNECTS"
                }));
            });
        }
    }

    private static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
    private static String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(StreamImporter::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }
}
