package londonSafeTravel.driver.tims;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import londonSafeTravel.driver.tims.geo.GeoObject;
import londonSafeTravel.schema.Location;
import londonSafeTravel.schema.document.Disruption;
import londonSafeTravel.schema.document.ManageDisruption;

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

    GeoObject geography;
    GeoObject geometry;

    String severity;

    public static void main(String[] argv) throws FileNotFoundException {
        if(argv.length == 0)
            throw new IllegalArgumentException("Usage: exe file.json");

        Type collectionType = new TypeToken<ArrayList<RoadDisruptionUpdate>>(){}.getType();
        Collection<RoadDisruptionUpdate> updates = new Gson().fromJson(new FileReader(argv[0]), collectionType);

        if(updates.isEmpty())
            return;

        ManageDisruption m = new ManageDisruption();
        updates.forEach(roadDisruptionUpdate -> {
            final Date time = roadDisruptionUpdate.currentUpdateDateTime;

            Disruption d = m.get(roadDisruptionUpdate.id);
            if(d == null)
                d = new Disruption();

            d.id = roadDisruptionUpdate.id;
            d.category = roadDisruptionUpdate.category;
            d.severity = roadDisruptionUpdate.severity;
            d.start = roadDisruptionUpdate.startDateTime;
            d.end = roadDisruptionUpdate.endDateTime;

            if(d.updates.isEmpty()
                    || !d.updates.get(d.updates.size() - 1).message.equals(roadDisruptionUpdate.currentUpdate))
            {
                Disruption.Update update = new Disruption.Update();
                update.message = roadDisruptionUpdate.currentUpdate;
                update.start = roadDisruptionUpdate.currentUpdateDateTime;
                update.end = roadDisruptionUpdate.currentUpdateDateTime;

                d.updates.add(update);
            }
            else
            {
                // This is a reference, it does not make a copy like C++ I guess(?)
                Disruption.Update update = d.updates.get(d.updates.size() - 1);
                update.end = roadDisruptionUpdate.currentUpdateDateTime;
            }

            m.set(d);
        });

        // Check if this disruption is in the db
        // if not -> create it
        // if yes -> update it

        // riondskljfnsdklòj with graph database get() and set()
    }
}