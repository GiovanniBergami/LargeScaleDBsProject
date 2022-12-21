package londonSafeTravel.OSMImporter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class StreamImporter {

    public static void main(String[] argv) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader r = factory.createXMLStreamReader(new FileReader("greater-london-latest.osm"));

        while(r.hasNext())
        {
            r.next();
            if(r.hasName())
                System.out.println(r.getLocalName());
        }
    }
}
