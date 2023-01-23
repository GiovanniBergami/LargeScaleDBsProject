package londonSafeTravel.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import londonSafeTravel.dbms.graph.ManageDisruption;
import org.neo4j.driver.Driver;

public class QueryDisruptionHandler extends Handler {
    ManageDisruption manageDisruption;

    public QueryDisruptionHandler(Driver driver) {
        manageDisruption = new ManageDisruption(driver);
    }

    public QueryDisruptionHandler() {
        manageDisruption = new ManageDisruption("neo4j://localhost:7687", "neo4j", "pass");
    }

    @Override
    public void handleRequest(HttpExchange exchange) throws Exception {
        var disruptions = manageDisruption.findDisruption();
        String json = new Gson().toJson(disruptions);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);

        exchange.getResponseBody().write(json.getBytes());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
    }
}
