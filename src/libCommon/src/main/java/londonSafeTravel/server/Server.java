package londonSafeTravel.server;

import com.sun.net.httpserver.HttpServer;
import londonSafeTravel.dbms.document.ConnectionMongoDB;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    public static void main(String[] argv) throws Exception {
        Driver neo4j = GraphDatabase.driver(
                "bolt://172.16.5.47",
                AuthTokens.basic("neo4j", "password"));

        ConnectionMongoDB mongoc = new ConnectionMongoDB("mongodb://172.16.5.43:27017");

        // Check if online
        neo4j.verifyConnectivity();

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);

        server.createContext("/opendoor", new ExceptionTester());
        server.createContext("/query.json", new QueryPointHandler(neo4j));
        server.createContext("/route.json", new RoutingHandler(neo4j));
        server.createContext("/disruptions.json", new QueryDisruptionHandler(neo4j));
        server.createContext("/heatmap.json", new HeatmapHandler(mongoc));
        server.createContext("/queryPOI.json", new POIHandler(mongoc));
        
        server.setExecutor(threadPoolExecutor);
        server.start();
    }
}
