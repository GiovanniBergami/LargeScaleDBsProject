package londonSafeTravel.driver.tims;

import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

import com.github.filosganga.geogson.gson.GeometryAdapterFactory;
import com.github.filosganga.geogson.model.*;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import londonSafeTravel.schema.GeoFactory;
import londonSafeTravel.schema.document.Disruption;
import londonSafeTravel.schema.document.ManageDisruption;


@SuppressWarnings("rawtypes")
public class RoadDisruptionUpdate {
    String id;
    String category;
    String subCategory;
    String comments;
    String currentUpdate;
    Date currentUpdateDateTime;
    Date startDateTime;
    Date endDateTime;
    Date lastModifiedTime;
    String levelOfInterest;
    String status;

    Point geography;
    Geometry geometry;

    String severity;

    private static class ProcessResult implements Serializable {
        Date time = new Date(0);
        HashSet<String> processed = new HashSet<>();
    }

    private static ManageDisruption manageDisruptionDocument;

    private static ProcessResult process(InputStreamReader fs, ProcessResult last, Date t) {
        Type collectionType = new TypeToken<ArrayList<RoadDisruptionUpdate>>() {
        }.getType();
        Collection<RoadDisruptionUpdate> updates = new GsonBuilder()
                .registerTypeAdapterFactory(new GeometryAdapterFactory())
                .create().fromJson(fs, collectionType);

        HashSet<String> explored = new HashSet<>();

        if (updates == null || updates.isEmpty())
            return null;

        if (t.before(last.time))
            throw new RuntimeException(
                    "I can only go forward in time! Current file was at " + t + " I can only go after " + last.time);

        updates.forEach(roadDisruptionUpdate -> {
            // Add this disruption to the explored set
            explored.add(roadDisruptionUpdate.id);

            Disruption d = manageDisruptionDocument.get(roadDisruptionUpdate.id);
            if (d == null)
                d = new Disruption();

            d.id = roadDisruptionUpdate.id;
            d.category = roadDisruptionUpdate.category;
            d.subCategory = roadDisruptionUpdate.subCategory;
            d.severity = roadDisruptionUpdate.severity;
            d.start = roadDisruptionUpdate.startDateTime;
            d.end = roadDisruptionUpdate.endDateTime;


            d.coordinates = new com.mongodb.client.model.geojson.Point(
                    GeoFactory.fromFilosgangaToMongo(roadDisruptionUpdate.geography));

            if (roadDisruptionUpdate.geometry == null)
                d.boundaries = null;
            else if (roadDisruptionUpdate.geometry.type() == Geometry.Type.POLYGON)
                d.boundaries = GeoFactory.fromFilosgangaToMongo((Polygon) roadDisruptionUpdate.geometry);
            else if (roadDisruptionUpdate.geometry.type() == Geometry.Type.MULTI_POLYGON)
                d.boundaries = GeoFactory.fromFilosgangaToMongo((MultiPolygon) roadDisruptionUpdate.geometry);
            else
                System.err.println(d.id + "\tUnknown type " + roadDisruptionUpdate.geometry.type());

            // @FIXME Why??
            if (d.updates == null)
                d.updates = new ArrayList<>();

            if (d.updates.isEmpty()
                    || !d.updates.get(d.updates.size() - 1).message.equals(roadDisruptionUpdate.currentUpdate)) {
                Disruption.Update update = new Disruption.Update();
                update.message = roadDisruptionUpdate.currentUpdate;
                update.start = roadDisruptionUpdate.currentUpdateDateTime;
                update.end = roadDisruptionUpdate.currentUpdateDateTime;

                d.updates.add(update);
            } else {
                // This is a reference, it does not make a copy like C++ I guess(?)
                Disruption.Update update = d.updates.get(d.updates.size() - 1);
                update.end = roadDisruptionUpdate.currentUpdateDateTime;
            }

            manageDisruptionDocument.set(d);
        });

        // Now we close disruptions that haven't been updated.
        // We remove from the set of disruptions processed last time the disruptions processed now; resulting in a
        // set with only disruptions that had expired
        HashSet<String> frontier = new HashSet<>(last.processed);
        frontier.removeAll(explored);

        frontier.forEach(terminatedDisruptionID -> {
            System.out.println("Closing disruption " + terminatedDisruptionID + " time " + t);

            Disruption toClose = manageDisruptionDocument.get(terminatedDisruptionID);
            toClose.end = t;
            manageDisruptionDocument.set(toClose);
        });

        ProcessResult r = new ProcessResult();
        r.time = t;
        r.processed = explored;

        return r;
    }

    public static void main(String[] argv) throws Exception {
        // Open connections to DBs
        manageDisruptionDocument = new ManageDisruption();

        ProcessResult state;

        // DEPRECATED IDK IDCz
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Try restore previous state from disk
        try {
            state = (ProcessResult) new ObjectInputStream(new FileInputStream("tims.roads.ser")).readObject();
        } catch (Exception e) {
            System.err.println("Failed to read from disk! Starting from 0. Error " + e.getMessage());
            state = new ProcessResult();
        }

        if (argv.length == 0) {
            System.out.println("Reading from stdin");
            var ret = process(new InputStreamReader(System.in), state, new Date());
            if(ret != null)
                state = ret;
        } else
            for (String filename : argv) {
                System.out.println("Parsing " + filename);
                Date t;
                int pos = filename.lastIndexOf('/');
                int ext = filename.lastIndexOf('.');
                try {
                    t = inputFormat.parse(filename.substring(pos + 1, ext));
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.err.println("Unable to parse " +
                            filename.substring(pos + 1, ext) + " as date, using current date :P");
                    t = new Date();
                }
                var ret = process(new FileReader(filename), state, t);
                if(ret != null)
                    state = ret;
            }

        // Save to disk
        new ObjectOutputStream(new FileOutputStream("tims.roads.ser")).writeObject(state);
    }
}