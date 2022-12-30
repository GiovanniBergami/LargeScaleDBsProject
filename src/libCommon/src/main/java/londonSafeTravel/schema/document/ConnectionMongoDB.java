package londonSafeTravel.schema.document;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class ConnectionMongoDB {

    static final private String uri = "mongodb://localhost:27017";
    private final MongoClient myClient = MongoClients.create(uri);
    private final MongoDatabase database = myClient.getDatabase("londonSafeTravel");

    public MongoDatabase giveDB(){
        CodecRegistry defaultCodecRegistry = MongoClientSettings.getDefaultCodecRegistry();
        CodecRegistry fromProvider = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(defaultCodecRegistry, fromProvider);
        return database.withCodecRegistry(pojoCodecRegistry);
    }



}
