package londonSafeTravel.OSMImporter;


import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import londonSafeTravel.dbms.graph.ManagePoint;
import londonSafeTravel.dbms.graph.ManageWay;
import londonSafeTravel.schema.Location;
import londonSafeTravel.schema.graph.Point;
import londonSafeTravel.schema.graph.Way;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class StreamImporter {
    private static HashMap<Long, Point> map;

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

    // @TODO Implement ONE-WAY streets and default access restrictions!
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

        ManageWay manageWay = new ManageWay("neo4j://localhost:7687", "neo4j", "pass");

        ArrayList<Way> ways = new ArrayList<>();
        long total = 0, totalWays = 0;
        final long cardinalityOverStime = 513192L;
        for (r.next(); r.hasNext(); r.next()) {
            if (!r.hasName())
                continue;

            // È via?
            final String currentNamespace = r.getNamespaceURI();
            if (!Objects.equals(r.getLocalName(), "way"))
                continue;

            ArrayList<Long> locations = new ArrayList<>();
            HashMap<String, String> tags = new HashMap<>();
            for (r.next(); !(r.isEndElement() && Objects.equals(r.getLocalName(), "way")); r.next()) {
                if (!r.isStartElement() || !r.hasName())
                    continue;
                final String currentNamespaceIn = r.getNamespaceURI();
                if (Objects.equals(r.getLocalName(), "nd")) {
                    locations.add(Long.parseLong(r.getAttributeValue(currentNamespaceIn, "ref")));
                } else if (Objects.equals(r.getLocalName(), "tag")) {
                    tags.put(r.getAttributeValue(currentNamespaceIn, "k"), r.getAttributeValue(currentNamespaceIn, "v"));
                }
            }
            //System.out.println(locations.size()+" "+tags.get("highway")+" "+tags.get("maxspeed"));
            if (!tags.containsKey("highway") || !highwayTarget.contains(tags.get("highway")))
                continue;

            for (int i = 1; i < locations.size(); i++) {
                Point p1 = map.get(locations.get(i - 1));
                Point p2 = map.get(locations.get(i));

                Way w = WaysFactory.getWay(tags, p1, p2);

                if(w != null)
                    ways.add(w);
            }

            totalWays ++;
            if(ways.size() > 900) {
                total += ways.size();

                System.out.println("About to push " + ways.size() + "\ttotal " + total + "\tso circa " +
                        (100.0 * (double)totalWays / (double)cardinalityOverStime) + " %");

                ways.sort(new Comparator<Way>() {
                    @Override
                    public int compare(Way lhs, Way rhs) {
                        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                        return lhs.p1.getId() > lhs.p1.getId() ? -1 : (lhs.p1.getId() < rhs.p1.getId()) ? 1 : 0;
                    }
                });

                System.out.println("sorted!");

                manageWay.addWays(ways);
                ways.clear();

                System.out.println("done");
            }
        }

        if(ways.size() > 0)
            manageWay.addWays(ways);
    }
}
