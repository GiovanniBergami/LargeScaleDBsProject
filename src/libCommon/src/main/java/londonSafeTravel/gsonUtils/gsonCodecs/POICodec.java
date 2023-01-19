package londonSafeTravel.gsonUtils.gsonCodecs;

import com.github.filosganga.geogson.gson.GeometryAdapterFactory;
import com.google.gson.*;
import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.Point;
import londonSafeTravel.gsonUtils.GsonFactory;
import londonSafeTravel.schema.GeoFactory;
import londonSafeTravel.schema.document.poi.PointOfInterest;
import londonSafeTravel.schema.document.poi.PointOfInterestOSM;

import java.lang.reflect.Type;

public class POICodec implements JsonDeserializer<PointOfInterest>, JsonSerializer<PointOfInterest> {
    @Override
    public PointOfInterest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String type;

        if(json.getAsJsonObject().get("type") == null)
            type = "???";
        else
            type = json.getAsJsonObject().get("type").getAsString();

        if(type.equals("OSM-POI")) {
            return GsonFactory.build().fromJson(json.getAsJsonObject(), PointOfInterestOSM.class);
        }

        return new GsonBuilder()
                .registerTypeAdapterFactory(new GeometryAdapterFactory())
                .registerTypeAdapter(Geometry.class, new MongoGeometryCodec())
                .registerTypeAdapter(Point.class, new MongoGeometryCodec())
                .create().fromJson(json.getAsJsonObject(), PointOfInterest.class);
    }

    @Override
    public JsonElement serialize(PointOfInterest src, Type typeOfSrc, JsonSerializationContext context) {
        var t = new GsonBuilder()
                .registerTypeAdapterFactory(new GeometryAdapterFactory())
                .registerTypeAdapter(Geometry.class, new MongoGeometryCodec())
                .registerTypeAdapter(Point.class, new MongoGeometryCodec())
                .create().toJsonTree(src, typeOfSrc);

        t.getAsJsonObject().addProperty("type", src.getType());
        t.getAsJsonObject().remove("coordinates");
        t.getAsJsonObject().add("coordinates",
                context.serialize(GeoFactory.toSgagna(GeoFactory.fromMongo(src.coordinates))));

        return t;
    }

    public static void main(String[] argv) {
        PointOfInterest test = GsonFactory.build().fromJson(
                        """
                                   {
                                          _id: ObjectId("63c7fd04471866042a9d524e"),
                                          coordinates: {
                                            type: 'Point',
                                            coordinates: [ -0.2957049777777778, 51.47126061111111 ]
                                          },
                                          name: 'Pagoda',
                                          perimeter: {
                                            type: 'LineString',
                                            coordinates: [
                                              [ -0.2956609, 51.4711974 ],
                                              [ -0.2957775, 51.4712026 ],
                                              [ -0.295831, 51.4712476 ],
                                              [ -0.2958252, 51.4712983 ],
                                              [ -0.295751, 51.4713409 ],
                                              [ -0.2956479, 51.4713339 ],
                                              [ -0.2955929, 51.4712894 ],
                                              [ -0.2955975, 51.471238 ],
                                              [ -0.2956609, 51.4711974 ]
                                            ]
                                          },
                                          poiID: '4806085',
                                          tags: {
                                            'name:ru': 'Пагода',
                                            historic: 'folly',
                                            man_made: 'tower',
                                            architect: 'William Chambers',
                                            name: 'Pagoda',
                                            tourism: 'attraction',
                                            'architect:wikidata': 'Q455155',
                                            wikidata: 'Q4091854',
                                            building: 'pagoda',
                                            height: '50',
                                            'tower:type': 'pagoda'
                                          },
                                          type: 'OSM-POI'
                                        }
                                """,
                        PointOfInterest.class
                );

        System.out.println(test.name);
    }
}
