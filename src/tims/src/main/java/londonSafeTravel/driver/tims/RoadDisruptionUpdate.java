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

    public static void main(String[] argv) throws FileNotFoundException {
        if(argv.length == 0)
            throw new IllegalArgumentException("Usage: exe file.json");

        Type collectionType = new TypeToken<ArrayList<RoadDisruptionUpdate>>(){}.getType();
        Collection<RoadDisruptionUpdate> updates = new Gson().fromJson(new FileReader(argv[0]), collectionType);

        updates.forEach(roadDisruptionUpdate -> {
            System.out.println(roadDisruptionUpdate.category);
        });

        // Check if this disruption is in the db
        // if not -> create it
        // if yes -> update it

        // riondskljfnsdklòj with graph database get() and set()
    }
}