package londonSafeTravel.OSMImporter;

import londonSafeTravel.dbms.graph.ManagePoint;
import londonSafeTravel.schema.graph.Point;

import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WayImporter {
    public static void main(String[] argv) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader r = factory.createXMLStreamReader(new FileReader("greater-london-latest.osm"));
        long id1,id2;

        for(r.next(); r.hasNext(); r.next()) {
            if (!r.hasName())
                continue;
            final String currentNamespace = r.getNamespaceURI();
            if(Objects.equals(r.getLocalName(),"way")){
                boolean endWay=false;
                ArrayList<Long> locations=new ArrayList<>();
                HashMap<String,String> tags= new HashMap<>();
                for(r.next(); !(r.isEndElement() && Objects.equals(r.getLocalName(),"way")); r.next()){
                    if(!r.isStartElement() || !r.hasName())
                        continue;
                    final String currentNamespaceIn = r.getNamespaceURI();
                    if(Objects.equals(r.getLocalName(),"nd")){
                        locations.add(Long.parseLong(r.getAttributeValue(currentNamespaceIn, "ref")));
                    }
                    else if(Objects.equals(r.getLocalName(),"tag")){
                        tags.put(r.getAttributeValue(currentNamespaceIn, "k"),r.getAttributeValue(currentNamespaceIn, "v"));
                    }
                }
                //System.out.println(locations.size()+" "+tags.get("highway")+" "+tags.get("maxspeed"));
            }
        }
    }
}
