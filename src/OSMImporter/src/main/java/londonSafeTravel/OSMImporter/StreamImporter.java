package londonSafeTravel.OSMImporter;


import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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


    public static void main(String[] argv) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader r = factory.createXMLStreamReader(new FileReader("greater-london-latest.osm"));
        HashMap<Long, Point> map = new HashMap<Long, Point>();
        ManageWay manageWay = new ManageWay("neo4j://localhost:7687", "neo4j", "pass");
        ManagePoint managePoint = new ManagePoint("neo4j://localhost:7687", "neo4j", "pass");
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
            System.out.println("Inserito punto con id: " + id);
            managePoint.addNode(p);
        }
        r = factory.createXMLStreamReader(new FileReader("greater-london-latest.osm"));
        long id1, id2;

        for (r.next(); r.hasNext(); r.next()) {
            if (!r.hasName())
                continue;
            final String currentNamespace = r.getNamespaceURI();
            if (Objects.equals(r.getLocalName(), "way")) {
                boolean endWay = false;
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
                if (!tags.containsKey("highway") || !Objects.equals(tags.get("highway"), "primary"))
                    continue;

                for (int i = 1; i < locations.size(); i++) {
                    Point p1 = map.get(locations.get(i - 1));
                    Point p2 = map.get(locations.get(i));
                    Way w = new Way();
                    w.name = tags.get("name");
                    w.p1 = p1;
                    w.p2 = p2;
                    manageWay.addWay(w);
                }
            }
        }
    }
}
