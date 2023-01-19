package londonSafeTravel.gsonUtils.gsonCodecs;

import com.github.filosganga.geogson.model.LineString;
import com.github.filosganga.geogson.model.Point;
import com.google.gson.*;
import com.mongodb.client.model.geojson.Geometry;
import londonSafeTravel.gsonUtils.GsonFactory;
import londonSafeTravel.schema.GeoFactory;

import java.lang.reflect.Type;

public class MongoGeometryCodec implements JsonDeserializer<Geometry> {
    @Override
    public Geometry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.getAsJsonObject().get("type") == null) {
            return null;
        }

        String type = json.getAsJsonObject().get("type").getAsString();

        if(type.equals("LineString"))
            return GeoFactory.fromFilosgangaToMongo(GsonFactory.build().fromJson(json, LineString.class));
        else if(type.equals("Point"))
            return GeoFactory.fromFilosgangaToMongo(GsonFactory.build().fromJson(json, Point.class));

        System.err.println("I cannot handle " + type + " at the moment :(");
        return null;
    }
}
