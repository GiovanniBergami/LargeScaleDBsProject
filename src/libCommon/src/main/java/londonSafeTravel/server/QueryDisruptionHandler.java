package londonSafeTravel.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import londonSafeTravel.dbms.graph.ManageDisruption;
import org.neo4j.driver.Driver;

import java.io.IOException;

public class QueryDisruptionHandler implements HttpHandler {
    ManageDisruption manageDisruption;

    public QueryDisruptionHandler(Driver driver) {
        manageDisruption = new ManageDisruption(driver);
    }

    public QueryDisruptionHandler() {
        manageDisruption = new ManageDisruption("neo4j://localhost:7687", "neo4j", "pass");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var disruptions = manageDisruption.findDisruption();
        String json = new Gson().toJson(disruptions);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.length());

        exchange.getResponseBody().write(json.getBytes());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }
}
