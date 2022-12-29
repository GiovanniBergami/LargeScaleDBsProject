package londonSafeTravel.schema.document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import org.apache.xerces.impl.xpath.regex.Match;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Aggregates.*;



public class TransitStopDAO {

    private ConnectionMongoDB connection = new ConnectionMongoDB();
    private MongoDatabase db = connection.giveDB();
    private MongoCollection collection = db.getCollection("TransitStop");


    /*
    Per ogni linea trovare,dato un giorno della settimana, il numero di closures
    che sono avvenute in totale su quella linea e la probabilità
    che la linea sia coinvolta in una closure
    dove 1 = evento certo, 0 = evento impossibile
     */

  /*
  Per ogni classe di public transportation disruption
   trovare la top 3 ( o 5) delle linee che sono più affette.
   */
// Da testare
    public Collection<Document> query3() {
        Bson match = match(eq("typeDisruption", "PUBLIC_TRANSPORT"));
        // Create the group stage
        Bson groupStage = Aggregates.group(
                "$terminatedDisruption.category",
                Accumulators.sum("count", 1),
                Accumulators.first("line", "$routes.line")
        );
        Bson sortStage = Aggregates.sort(Sorts.descending("count"));
        Bson limit = limit(3);
        // Combine the stages into a pipeline
        List<Bson> pipeline = Arrays.asList(match, groupStage, sortStage,limit);

        // Execute the aggregation
        Collection<Document> result = collection.aggregate(pipeline).into(new ArrayList<>());
        return result;
    }
}
