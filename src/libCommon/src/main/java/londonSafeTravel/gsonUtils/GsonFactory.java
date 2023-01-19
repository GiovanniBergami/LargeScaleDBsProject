package londonSafeTravel.gsonUtils;

import com.github.filosganga.geogson.gson.GeometryAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.Point;
import londonSafeTravel.gsonUtils.gsonCodecs.MongoGeometryCodec;
import londonSafeTravel.gsonUtils.gsonCodecs.POICodec;
import londonSafeTravel.gsonUtils.gsonCodecs.POIOSMCodec;
import londonSafeTravel.schema.document.poi.PointOfInterest;
import londonSafeTravel.schema.document.poi.PointOfInterestOSM;

public class GsonFactory {
    private static final GsonBuilder builder = new GsonBuilder()
            .registerTypeAdapter(PointOfInterest.class, new POICodec())
            .registerTypeAdapter(PointOfInterestOSM.class, new POIOSMCodec())
            .registerTypeAdapterFactory(new GeometryAdapterFactory())
            .registerTypeAdapter(Geometry.class, new MongoGeometryCodec())
            .registerTypeAdapter(Point.class, new MongoGeometryCodec());

    public static Gson build() {
        return builder.create();
    }

}
