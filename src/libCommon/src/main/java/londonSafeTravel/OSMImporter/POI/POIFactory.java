package londonSafeTravel.OSMImporter.POI;

import londonSafeTravel.schema.Location;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.*;

public class POIFactory {
    private static HashMap<String, HashSet<String>> targets = new HashMap<>(){{
        put("historic", null);
        put("landmark", new HashSet<>() {{
            add("statue"); add("memorial_plaque");
        }});
        put("memorial", null);
    }};

    private static boolean isEndTag(XMLStreamReader reader, String type) {
        return reader.isEndElement() && Objects.equals(reader.getLocalName(), type) ;
    }

    public static POI parse(XMLStreamReader reader, HashMap<Long, Location> map) throws Exception {
        String type = reader.getLocalName();
        if(!type.equals("node") && !type.equals("way"))
            return null;

        POI current = type.equals("node") ? new Point() : new Way();

        if(type.equals("node")) {
            final String currentNamespace = reader.getNamespaceURI();
            final double lat = Double.parseDouble(reader.getAttributeValue(currentNamespace, "lat"));
            final double lon = Double.parseDouble(reader.getAttributeValue(currentNamespace, "lon"));
            final long id = Long.parseLong(reader.getAttributeValue(currentNamespace, "id"));

            map.put(id, new Location(lat, lon));
            ((Point)current).setCentrum(new Location(lat, lon));
        }

        List<Long> locations = new ArrayList<>();
        HashMap<String, String> tags = new HashMap<>();
        for(; !isEndTag(reader, type); reader.next()) {
            if (!reader.isStartElement() || !reader.hasName())
                continue;

            final String currentNamespaceIn = reader.getNamespaceURI();
            // What kind of element are we parsing rn?
            if (Objects.equals(reader.getLocalName(), "nd")) {
                // It's a node
                long id = Long.parseLong(reader.getAttributeValue(currentNamespaceIn, "ref"));
                locations.add(id);
            } else if (Objects.equals(reader.getLocalName(), "tag")) {
                // It's a tag
                tags.put(
                        reader.getAttributeValue(currentNamespaceIn, "k"),
                        reader.getAttributeValue(currentNamespaceIn, "v"));
            }
        }

        boolean ok = false;
        for (Map.Entry<String, String> tag : tags.entrySet()) {
            var e = targets.get(tag.getKey());
            if(e == null || e.contains(tag.getValue())) {
                ok = true;

                current.className = tag.getKey();
                current.type = tag.getValue();

                break;
            }
        }

        if(!ok)
            return null;

        current.name = tags.get("name");

        if(type.equals("way")) {
            Way w = (Way) current;
            w.setPerimeter(null); //@todo
        }



        return current;
    }

    public static void main(String[] argv) {
        HashMap<Long, Location> map = new HashMap<>();

    }
}
