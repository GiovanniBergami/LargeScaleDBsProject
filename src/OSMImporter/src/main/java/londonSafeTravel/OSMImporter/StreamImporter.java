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
        HashMap<Long, Location> map=new HashMap<Long, Location>();

        for(r.next(); r.hasNext(); r.next())
        {
            if(! r.isStartElement())
                continue;

            if(! r.hasName() || ! Objects.equals(r.getLocalName(), "node"))
                continue;

            final String currentNamespace = r.getNamespaceURI();
            double lat = Double.parseDouble(r.getAttributeValue(currentNamespace, "lat"));
            double lon = Double.parseDouble(r.getAttributeValue(currentNamespace, "lon"));
            Location location=new Location(lat,lon);
            Long id=Long.parseLong(r.getAttributeValue(currentNamespace, "id"));
            map.put(id,location);
            System.out.println("Inserito punto con id: "+id);
            System.out.println("La sua location è "+map.get(id));
        }
    }
}
