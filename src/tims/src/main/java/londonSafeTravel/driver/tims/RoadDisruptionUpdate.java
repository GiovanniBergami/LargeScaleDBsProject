package londonSafeTravel.driver.tims;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import londonSafeTravel.schema.Location;

public class RoadDisruptionUpdate {
    static class Geometry
    {
        Collection<Collection<Location>> coordinates;
    }

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

    Location point;
    Geometry geometry;

    public static void main(String[] argv) throws FileNotFoundException {
        System.out.println(argv.length);
        System.out.println(argv[0]);
        return;
        //if(argv.length < 2)
        //    throw new IllegalArgumentException("Usage: exe file.json");

       // Type collectionType = new TypeToken<ArrayList<RoadDisruptionUpdate>>(){}.getType();
       // Collection<RoadDisruptionUpdate> updates = new Gson().fromJson(new FileReader(argv[1]), collectionType);


    }
}