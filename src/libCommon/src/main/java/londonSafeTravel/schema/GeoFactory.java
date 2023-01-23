package londonSafeTravel.schema;

import com.mongodb.client.model.geojson.*;
import org.neo4j.driver.types.Point;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GeoFactory {
    public static Location fromNeo4j(Point p) {
        return new Location(p.y(), p.x());
    }

    public static Location fromFilosgangaToLocation(com.github.filosganga.geogson.model.Point p) {
        return new Location(p.lat(), p.lon());
    }

    public static Location fromMongo(com.mongodb.client.model.geojson.Point point) {
        return new Location(
                point.getCoordinates().getValues().get(1),
                point.getCoordinates().getValues().get(0)
        );
    }

    public static com.mongodb.client.model.geojson.Point convertToMongo(Location location) {
        return new com.mongodb.client.model.geojson.Point(new Position(
                location.getLongitude(),
                location.getLatitude()
        ));
    }

    public static LineString convertToMongo(List<Location> line) {
        return new LineString(
                line.stream()
                        .map(GeoFactory::convertToMongo)
                        .map(com.mongodb.client.model.geojson.Point::getPosition)
                        .collect(Collectors.toList())
        );
    }

    public static Polygon convertToMongo(Location.Polygon poly) {
        return new Polygon(convertToMongo2(poly));
    }

    public static PolygonCoordinates convertToMongo2(Location.Polygon poly) {
        return new PolygonCoordinates(
                poly.outer.stream()
                        .map(GeoFactory::convertToMongo)
                        .map(com.mongodb.client.model.geojson.Point::getPosition)
                        .collect(Collectors.toList()),
                poly.inner.stream()
                        .map(GeoFactory::convertToMongo)
                        .map(com.mongodb.client.model.geojson.Point::getPosition)
                        .collect(Collectors.toList())
        );
    }

    public static MultiPolygon convertToMongo2(List<Location.Polygon> polygons) {
        return new MultiPolygon(
                polygons.stream()
                        .map(GeoFactory::convertToMongo2)
                        .collect(Collectors.toList())
        );
    }

    public static com.mongodb.client.model.geojson.Point fromFilosgangaToMongo(
            com.github.filosganga.geogson.model.Point point) {
        return convertToMongo(new Location(point.lat(), point.lon()));
    }

    public static LineString fromFilosgangaToMongo(
            com.github.filosganga.geogson.model.LineString line) {
        return new LineString(
                StreamSupport.stream(line.points().spliterator(), false)
                        .map(GeoFactory::fromFilosgangaToMongo)
                        .map(com.mongodb.client.model.geojson.Point::getPosition)
                        .collect(Collectors.toList()));
    }

    public static Polygon fromFilosgangaToMongo(
            com.github.filosganga.geogson.model.Polygon polygon) {
        if (polygon.holes().iterator().hasNext())
            return new Polygon(
                    fromFilosgangaToMongo(
                            polygon.linearRings().iterator().next())
                            .getCoordinates(),

                    fromFilosgangaToMongo(
                            polygon.holes().iterator().next())
                            .getCoordinates()
            );
        else
            return new Polygon(
                    fromFilosgangaToMongo(polygon.linearRings().iterator().next()).getCoordinates()
            );
    }

    public static MultiPolygon fromFilosgangaToMongo(
            com.github.filosganga.geogson.model.MultiPolygon multiPolygon) {
        return new MultiPolygon(
                StreamSupport.stream(multiPolygon.polygons().spliterator(), false)
                        .map(GeoFactory::fromFilosgangaToMongo)
                        .map(Polygon::getCoordinates)
                        .collect(Collectors.toList())
        );
    }

    public static com.github.filosganga.geogson.model.Point toSgagna(Location p) {
        return com.github.filosganga.geogson.model.Point
                .from(p.getLongitude(), p.getLatitude());
    }
}
