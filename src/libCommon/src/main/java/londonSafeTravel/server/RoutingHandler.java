package londonSafeTravel.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import londonSafeTravel.dbms.graph.ManageRouting;
import org.apache.hc.core5.net.URIBuilder;
import org.neo4j.driver.Driver;

public class RoutingHandler extends Handler {
    ManageRouting manageRouting;

    public RoutingHandler(Driver driver) {
        manageRouting = new ManageRouting(driver);
    }

    public RoutingHandler() {
        manageRouting = new ManageRouting("neo4j://localhost:7687", "neo4j", "pass");
    }
    @Override
    public void handleRequest(HttpExchange exchange) throws Exception {
        var uriParsed = new URIBuilder(exchange.getRequestURI());

        long start = Long.parseLong(uriParsed.getFirstQueryParam("start").getValue());
        long end = Long.parseLong(uriParsed.getFirstQueryParam("end").getValue());
        boolean dis = Boolean.parseBoolean(uriParsed.getFirstQueryParam("considerDisruptions").getValue());

        String type = uriParsed.getFirstQueryParam("type") != null ?
                uriParsed.getFirstQueryParam("type").getValue() : "car";

        System.out.println(uriParsed.getFirstQueryParam("type"));

        // @TODO Handle errors here
        System.out.println(start + "\t" + end);
        var route = manageRouting.route1(start, end, type, dis);
        String json = new Gson().toJson(route);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);

        exchange.getResponseBody().write(json.getBytes());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
    }
}
