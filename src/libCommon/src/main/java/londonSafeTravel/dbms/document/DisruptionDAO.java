package londonSafeTravel.dbms.document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import londonSafeTravel.schema.document.Disruption;


import static com.mongodb.client.model.Filters.*;


public class DisruptionDAO {
    private final MongoCollection<Disruption> collection;

    public DisruptionDAO() {
        this(new ConnectionMongoDB());
    }

    public DisruptionDAO(ConnectionMongoDB connection){
        MongoDatabase db = connection.giveDB();
        this.collection = db.getCollection("Disruption", Disruption.class);
    }

    /**
     * IL metodo get deve restituire una disruption dato un id in ingresso
     * Per il set, data una disruption, la crea/rimpiazza se esiste già
     * @param id a disruption's id
     * @return a disruption or null if not exists
     *
    */
    public Disruption get(String id) {
        return collection.find(eq("id", id)).first();
    }

    public void set(Disruption d){
        var docs = collection.find(eq("id", d.id));
        var resultDoc = docs.first();

        try {
            if (resultDoc == null) {
                collection.insertOne(d);
            }else{
                collection.replaceOne(eq("id", d.id), d);
            }
        }catch (MongoException me) {
            System.err.println("Unable to update due to an error: " + me);
        }
    }

    public static void main(String[] argv) {
        DisruptionDAO man = new DisruptionDAO();
        /*
        * Non si può fare test perchè gli esempi sono fatti male con gli id int e non stringhe
        * */
        man.get("1");
    }

}
