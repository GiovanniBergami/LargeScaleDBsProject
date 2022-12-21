package londonSafeTravel.schema.document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConnectionMongoDB {

    static final private String uri = "mongodb://localhost:27017";
    private final MongoClient myClient = MongoClients.create(uri);
    private final MongoDatabase database = myClient.getDatabase("londonSafeTravel");

    public MongoDatabase giveDB(){
        return database;
    }



}
