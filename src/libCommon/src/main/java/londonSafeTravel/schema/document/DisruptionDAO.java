package londonSafeTravel.schema.document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.mongodb.client.model.Accumulators.addToSet;
import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.geoWithin;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;



public class DisruptionDAO {
    private ConnectionMongoDB connection = new ConnectionMongoDB();
    private MongoDatabase db = connection.giveDB();
    private MongoCollection collection = db.getCollection("Disruption");

    public void printAll() {

        try (
                MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }

    }


    public static void main(String[] argv)
    {
        DisruptionDAO disDAO = new DisruptionDAO();

        disDAO.query2(-1, 100, -1, 90).forEach(d -> {
            System.out.println(d.toJson());
        });
    }

    /* Find the most common disruption in a given area (per ogni severity) */

    /*
        Medium:
            Broken traffic light 10
            ...
        High:
            Collision 20
     */


    /*
        This function will return a list witch contains the most common disruption in order to severity
    */

    public Collection<Document> query2(double minLong, double maxLong, double minLat, double maxLat){
        Polygon region = new Polygon(Arrays.asList(
                new Position(minLong, minLat),
                new Position(maxLong, minLat),
                new Position(maxLong, maxLat),
                new Position(minLong, maxLat),
                new Position(minLong, minLat)
        ));

        Bson inSquare = geoWithin("coordinates", region);
        /*
        Bson group1 = new Document("$group", new Document("_id", new Document("category", "$category")
                .append("severity", "$severity"))
                .append("nCategory", new Document("$sum",1L)));
        Bson project1 = project(fields(excludeId(), computed("category", "$_id.category"),
                computed("severity", "$_id.severity"), computed("nCategory", "$nCategory")));
        Bson group2 = group("$severity",Accumulators.sum("nCategory", 1L));
        Bson sort = sort(descending("nCategory"));
        Bson project2 = project(fields(excludeId(), computed("severity", "$_id"), include("nCategory")));
        */

        Bson group1 = new Document("$group", new Document("_id", new Document("category", "$category").append("severity", "$severity"))
                .append("nDisruptions", new Document("$sum", 1L))); // per ogni categoria mi conta quante disruption ci sono
        Bson group2 = group("$_id.severity", Accumulators.sum("numCategory",1L ), addToSet("category", "$_id.category"));
        Bson sort = sort(descending("numCategory"));
        Bson project = project(fields(excludeId(), computed("category", "$_id"), include("category", "numCategory")));


        /*Bson group = group("$severity", Accumulators.sum("count", 1L), Accumulators.push("category", "$category"));
        Bson sort = sort(descending("count"));
        //Bson project = project(fields(excludeId(),include("count", "name")));
        Collection results = collection.aggregate(Arrays.asList(Aggregates.match(inSquare),group, sort)).into(new ArrayList<>());
*/
        ArrayList<Document> results = new ArrayList<>();
        collection.aggregate(Arrays.asList(group1, group2,sort, project))
                .forEach(doc -> {
                    results.add((Document) doc);
                });

        return results;
    }

    /*
    Per ogni linea trovare per ogni giorno della settimana il numero di closures e la probabilità
    che essa sia coinvolta in una closure dove 1 = evento certo, 0 = evento impossibile
     */




}
