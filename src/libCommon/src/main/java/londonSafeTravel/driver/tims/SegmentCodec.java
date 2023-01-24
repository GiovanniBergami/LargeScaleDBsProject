package londonSafeTravel.driver.tims;

import com.github.filosganga.geogson.gson.GeometryAdapterFactory;
import com.github.filosganga.geogson.model.LineString;
import com.github.filosganga.geogson.model.Point;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import londonSafeTravel.schema.document.Disruption;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.stream.Collectors;

class SegmentCodec implements JsonDeserializer<Street.Segment> {
    private static final Gson parser = new GsonBuilder()
                .registerTypeAdapterFactory(new GeometryAdapterFactory()).create();

    private static final Type collectionType = new TypeToken<ArrayList<ArrayList<Double>>>() {
    }.getType();

    @Override
    public Street.Segment deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var jsonSegment = json.getAsJsonObject();
        var segment = new Street.Segment();

        var jsonStringLineString = jsonSegment.get("lineString").getAsString();
        ArrayList<ArrayList<Double>> list = parser.fromJson(jsonStringLineString, collectionType);

        var coordinates = list.stream()
                .map(coor -> Point.from(coor.get(0), coor.get(1)))
                .toList();

        if(coordinates.size() >= 2)
            segment.lineString = LineString.of(coordinates);

        return segment;
    }
}
