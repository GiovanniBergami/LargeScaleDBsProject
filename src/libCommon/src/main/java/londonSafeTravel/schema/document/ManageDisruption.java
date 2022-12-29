package londonSafeTravel.schema.document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


import londonSafeTravel.schema.Location;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;


import com.mongodb.client.model.*;

import java.util.Collection;


public class ManageDisruption {

    private ConnectionMongoDB connection = new ConnectionMongoDB();
    private MongoDatabase db = connection.giveDB();
    private MongoCollection collection = db.getCollection("Disruption");

    /**
     * IL metodo get deve restituire una disruption dato un id in ingresso
     * Per il set, data una disruption, la crea/rimpiazza se esiste già
     * @param id a disruption's id
     * @return a disruption or null if not exists
     *
    */


    public Disruption get(String id){
        Disruption disruption = new Disruption();
        Document resultDoc = (Document) collection.find(eq("id", id)).first();

        if (resultDoc != null) {

                disruption.id = resultDoc.getString("id");
                disruption.type = resultDoc.getString("type");
                disruption.start = resultDoc.getDate("start");
                disruption.end = resultDoc.getDate("end");
                disruption.coordinates = resultDoc.get("coordinates", Location.class);
                disruption.severity = resultDoc.getString("severity");
                disruption.category = resultDoc.getString("category");
                return disruption;
        }
        return null;
    }

    public void set(Disruption d){
        var docs = collection.find(eq("id", d.id));

        Document resultDoc = (Document) docs.first();
        try {
        if (resultDoc == null) {

            collection.insertOne(new Document("id", d.id)
                    .append("type", d.type)
                    .append("start", d.start)
                    .append("end", d.end)
                    .append("coordinates", d.coordinates)
                    .append("severity", d.severity)
                    .append("category", d.category)

            );
        }else{
            collection.updateOne(eq("id", d.id),Updates.combine(
                            Updates.set("type", d.type),
                            Updates.set("start", d.start),
                            Updates.set("end", d.end),
                            Updates.set("coordinates", d.coordinates),
                            Updates.set("severity", d.severity),
                            Updates.set("category", d.category)
                            )
                    );
        }
        }catch (MongoException me) {
            System.err.println("Unable to update due to an error: " + me);
        }
    }

    public static void main(String[] argv) {
        ManageDisruption man = new ManageDisruption();
        /*
        * Non si può fare test perchè gli esempi sono fatti male con gli id int e non stringhe
        * */
        man.get("1");
    }

}
