package londonSafeTravel.OSMImporter.POI;

import londonSafeTravel.dbms.document.PointOfInterestDAO;
import londonSafeTravel.schema.GeoFactory;
import londonSafeTravel.schema.Location;
import londonSafeTravel.schema.document.ConnectionMongoDB;
import londonSafeTravel.schema.document.poi.PointOfInterest;
import londonSafeTravel.schema.document.poi.PointOfInterestOSM;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.FileReader;
import java.util.*;

public class POIFactory {
    /**
     * Filters for this POIs <tag, value> if value is null, it'll take all elements tag=*
     */
    private static final HashMap<String, HashSet<String>> targets = new HashMap<>(){{
        put("historic", null);
        put("landmark", new HashSet<>() {{
            add("statue"); add("memorial_plaque");
        }});
        put("memorial", null);
        put("tourism", null);
    }};

    private static boolean isEndTag(XMLStreamReader reader, String type) {
        return reader.isEndElement() && Objects.equals(reader.getLocalName(), type) ;
    }

    public static PointOfInterest convertToMongo(POI poi) {
        var mongoPoint = new PointOfInterestOSM();

        mongoPoint.poiID = String.valueOf(poi.osmID);
        mongoPoint.coordinates = GeoFactory.convertToMongo(poi.getCentrum());
        mongoPoint.name = poi.name;
        mongoPoint.tags = poi.osmTags;
        if(poi instanceof  Way)
            mongoPoint.perimeter = GeoFactory.convertToMongo(((Way)poi).getPerimeter());

        return mongoPoint;
    }

    public static POI parse(XMLStreamReader reader, HashMap<Long, Location> map) throws Exception {
        String type = reader.getLocalName();
        if(!type.equals("node") && !type.equals("way"))
            return null;

        POI current = type.equals("node") ? new Point() : new Way();

        final String currentNamespace = reader.getNamespaceURI();
        if(type.equals("node")) {
            final double lat = Double.parseDouble(reader.getAttributeValue(currentNamespace, "lat"));
            final double lon = Double.parseDouble(reader.getAttributeValue(currentNamespace, "lon"));
            final long id = Long.parseLong(reader.getAttributeValue(currentNamespace, "id"));

            map.put(id, new Location(lat, lon));
            ((Point)current).setCentrum(new Location(lat, lon));
        }

        current.osmID = Long.parseLong(reader.getAttributeValue(currentNamespace, "id"));

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
            if(targets.containsKey(tag.getKey()) && (e == null || e.contains(tag.getValue()))) {
                ok = true;

                current.className = tag.getKey();
                current.type = tag.getValue();

                break;
            }
        }

        if(!ok)
            return null;

        current.name = tags.get("name");
        current.osmTags = tags;

        if(type.equals("way")) {
            Way w = (Way) current;
            for(Long id : locations) {
                w.addPerimeterPoint(map.get(id));
            }
        }

        return current;
    }

    private static final String filenameDefault = "examples/greater-london-latest.osm";

    public static void main(String[] argv) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        final String filename = argv.length == 0 ? filenameDefault : argv[0];

        XMLStreamReader r = factory.createXMLStreamReader(new FileReader(filename));

        HashMap<Long, Location> map = new HashMap<>();

        PointOfInterestDAO poiDAO = new PointOfInterestDAO(new ConnectionMongoDB());

        for (r.next(); r.hasNext(); r.next()) {
            if (!r.isStartElement())
                continue;

            var poi = parse(r, map);
            if(poi == null)
                continue;

            poiDAO.insert(convertToMongo(poi));

            System.out.println(
                    poi.osmID + "\t" + (poi instanceof Point) + "\t" +
                           poi.className + "\t" + poi.name + "\t" + poi.getCentrum());
        }
    }
}
