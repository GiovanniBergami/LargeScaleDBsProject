package londonSafeTravel.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import londonSafeTravel.dbms.graph.ManageRouting;
import org.apache.hc.core5.net.URIBuilder;
import org.neo4j.driver.Driver;

import java.io.IOException;

public class RoutingHandler implements HttpHandler {
    ManageRouting manageRouting;

    public RoutingHandler(Driver driver) {
        manageRouting = new ManageRouting(driver);
    }

    public RoutingHandler() {
        manageRouting = new ManageRouting("neo4j://localhost:7687", "neo4j", "pass");
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var uriParsed = new URIBuilder(exchange.getRequestURI());

        long start = Long.parseLong(uriParsed.getFirstQueryParam("start").getValue());
        long end = Long.parseLong(uriParsed.getFirstQueryParam("end").getValue());
        String type = uriParsed.getFirstQueryParam("type") != null ?
                uriParsed.getFirstQueryParam("type").getValue() : "car";

        System.out.println(uriParsed.getFirstQueryParam("type"));

        // @TODO Handle errors here
        System.out.println(start + "\t" + end);
        var route = manageRouting.route1(start, end, type);
        String json = new Gson().toJson(route);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);

        exchange.getResponseBody().write(json.getBytes());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }
}
