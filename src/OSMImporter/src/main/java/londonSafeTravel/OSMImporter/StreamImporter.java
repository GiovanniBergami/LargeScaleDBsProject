package londonSafeTravel.OSMImporter;


import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Objects;

import javax.xml.parsers.*;

import londonSafeTravel.schema.Location;
import londonSafeTravel.schema.graph.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class StreamImporter {

    public static void main(String[] argv) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader r = factory.createXMLStreamReader(new FileReader("greater-london-latest.osm"));
        HashMap<Integer, Location> map=new HashMap<Integer, Location>();
        while(r.hasNext())
        {
            r.next();
            if(r.hasName()) {
                if(Objects.equals(r.getLocalName(), "node")) {
                    System.out.println(r.getName());
                    System.out.println("Attributi: "+r.getAttributeName(0)+"= "+r.getAttributeValue(0));
                }
            }
        }
    }
 /*
public static void main(String[] argv) throws ParserConfigurationException, IOException, SAXException, XMLStreamException {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader r = factory.createXMLStreamReader(new FileReader("greater-london-latest.osm"));
    HashMap<Integer, Location> map=new HashMap<Integer, Location>();
    while(r.hasNext()){
        r.next();
        if(r.hasName()){
            double lat = Double.parseDouble(r.getAttributeValue(3));
            double lon = Double.parseDouble(r.getAttributeValue(4));
            Location loc = new Location(lat,lon);
            map.put(Integer.parseInt(r.getAttributeValue(0)),loc);
            System.out.println(r.getName());
        }
    }
    System.out.println("Esco");
    for(Integer key : map.keySet()) {
        System.out.println(map.get(key));
    }

    }*/
}
