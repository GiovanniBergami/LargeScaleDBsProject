package londonSafeTravel.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import londonSafeTravel.dbms.graph.RoutingDAO;

import londonSafeTravel.schema.graph.Point;
import org.apache.hc.core5.net.URIBuilder;
import org.neo4j.driver.Driver;

class QueryPointHandler extends Handler {

    RoutingDAO routingDAO;

    public QueryPointHandler(Driver driver) {
        routingDAO = new RoutingDAO(driver);
    }
    public QueryPointHandler() {
        routingDAO = new RoutingDAO("neo4j://localhost:7687", "neo4j", "pass");
    }

    @Override
    public void handleRequest(HttpExchange exchange) throws Exception {
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

        Point target = routingDAO.nearestNode(lat, lon, type);
        String json = new Gson().toJson(target);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);

        exchange.getResponseBody().write(json.getBytes());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
    }
}
