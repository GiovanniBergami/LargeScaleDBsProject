package londonSafeTravel.OSMImporter.POI;

import londonSafeTravel.schema.Location;

import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public class POIFactory {
    private static HashMap<String, HashSet<String>> targets = new HashMap<>(){{
        put("historic", null);
        put("landmark", new HashSet<>() {{
            add("statue"); add("memorial_plaque");
        }});
        put("memorial", null);
    }};

    private static boolean isEndTag(XMLStreamReader reader) {
        return reader.isEndElement();
    }

    public static POI parse(XMLStreamReader reader, HashMap<Long, Location> map) {
        String type = reader.getLocalName();
        if(!type.equals("node") && !type.equals("way"))
            return null;

        POI current = type.equals("node") ? new Point() : new Way();

        return null;
    }

    public static void main(String[] argv) {
        HashMap<Long, Location> map = new HashMap<>();

    }
}
