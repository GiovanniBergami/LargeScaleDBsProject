package londonSafeTravel.schema;

import com.mongodb.client.model.geojson.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GeoFactory {

    public static Location fromFilosgangaToLocation(com.github.filosganga.geogson.model.Point p) {
        return new Location(p.lat(), p.lon());
    }

    public static Position convertToMongo(Location location) {
        return new Position(
                location.getLongitude(),
                location.getLatitude()
        );
    }

    public static LineString convertToMongo(List<Location> line) {
        return new LineString(line.stream().map(GeoFactory::convertToMongo).collect(Collectors.toList()));
    }

    public static Polygon convertToMongo(Location.Polygon poly) {
        return new Polygon(convertToMongo2(poly));
    }

    public static PolygonCoordinates convertToMongo2(Location.Polygon poly) {
        return new PolygonCoordinates(
                poly.outer.stream().map(GeoFactory::convertToMongo).collect(Collectors.toList()),
                poly.inner.stream().map(GeoFactory::convertToMongo).collect(Collectors.toList())
        );
    }

    public static MultiPolygon convertToMongo2(List<Location.Polygon> polygons) {
        return new MultiPolygon(
                polygons.stream().map(GeoFactory::convertToMongo2).collect(Collectors.toList())
        );
    }

    public static Position fromFilosgangaToMongo(com.github.filosganga.geogson.model.Point point) {
        return convertToMongo(new Location(point.lat(), point.lon()));
    }

    public static LineString fromFilosgangaToMongo(com.github.filosganga.geogson.model.LineString line) {
        return new LineString(
                StreamSupport.stream(line.points().spliterator(), false)
                        .map(GeoFactory::fromFilosgangaToMongo).collect(Collectors.toList()));
    }

    public static Polygon fromFilosgangaToMongo(com.github.filosganga.geogson.model.Polygon polygon) {
        if(polygon.holes().iterator().hasNext())
            return new Polygon(
                    fromFilosgangaToMongo(polygon.linearRings().iterator().next()).getCoordinates(),
                    fromFilosgangaToMongo(polygon.holes().iterator().next()).getCoordinates()
            );
        else
            return new Polygon(
                    fromFilosgangaToMongo(polygon.linearRings().iterator().next()).getCoordinates()
            );
    }

    public static MultiPolygon fromFilosgangaToMongo(com.github.filosganga.geogson.model.MultiPolygon multiPolygon) {
        return new MultiPolygon(
                StreamSupport.stream(multiPolygon.polygons().spliterator(), false)
                        .map(GeoFactory::fromFilosgangaToMongo)
                        .map(Polygon::getCoordinates)
                        .collect(Collectors.toList())
        );
    }
}
