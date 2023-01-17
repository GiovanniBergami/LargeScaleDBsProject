package londonSafeTravel.server;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    public static void main(String[] argv) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);

        server.createContext("/query.json", new QueryPointHandler());
        server.createContext("/route.json", new RoutingHandler());
        server.createContext("/disruptions.json", new QueryDisruptionHandler());
        server.createContext("/heatmap.json", new HeatmapHandler());
        server.createContext("/queryPOI.json", new POIHandler());
        
        server.setExecutor(threadPoolExecutor);
        server.start();
    }
}
