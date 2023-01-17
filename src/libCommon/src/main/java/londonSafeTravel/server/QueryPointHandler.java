package londonSafeTravel.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import londonSafeTravel.dbms.graph.ManageRouting;

import java.io.IOException;

import londonSafeTravel.schema.graph.Point;
import org.apache.hc.core5.net.URIBuilder;
import org.neo4j.driver.Driver;

class QueryPointHandler implements HttpHandler {

    ManageRouting manageRouting;

    public QueryPointHandler(Driver driver) {
        manageRouting = new ManageRouting(driver);
    }
    public QueryPointHandler() {
        manageRouting = new ManageRouting("neo4j://localhost:7687", "neo4j", "pass");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        var uriParsed = new URIBuilder(exchange.getRequestURI());

        double lat = Double.parseDouble(uriParsed.getFirstQueryParam("latitude").getValue());
        double lon = Double.parseDouble(uriParsed.getFirstQueryParam("longitude").getValue());
        String type = uriParsed.getFirstQueryParam("type") != null ?
                uriParsed.getFirstQueryParam("type").getValue() : "car";

        Point target = manageRouting.nearestNode(lat, lon, type);
        String json = new Gson().toJson(target);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.length());

        exchange.getResponseBody().write(json.getBytes());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }
}
