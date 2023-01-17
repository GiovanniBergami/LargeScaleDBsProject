package londonSafeTravel.server;

import com.sun.net.httpserver.HttpServer;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    public static void main(String[] argv) throws Exception {
        Driver neo4j = GraphDatabase.driver(
                "bolt://172.16.5.47:7687",
                AuthTokens.basic("neo4j", "password"));

        // Check if online
        neo4j.verifyConnectivity();

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);

        server.createContext("/query.json", new QueryPointHandler(neo4j));
        server.createContext("/route.json", new RoutingHandler(neo4j));
        server.createContext("/disruptions.json", new QueryDisruptionHandler(neo4j));
        server.createContext("/heatmap.json", new HeatmapHandler());
        server.createContext("/queryPOI.json", new POIHandler());
        
        server.setExecutor(threadPoolExecutor);
        server.start();
    }
}
