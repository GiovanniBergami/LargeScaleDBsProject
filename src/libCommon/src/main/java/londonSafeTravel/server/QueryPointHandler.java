package londonSafeTravel.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import londonSafeTravel.dbms.graph.ManageRouting;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import londonSafeTravel.schema.graph.Point;
import org.apache.hc.client5.http.utils.URIUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

class QueryPointHandler implements HttpHandler {

    ManageRouting manageRouting;
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

        List<NameValuePair> params = URLEncodedUtils.parse(exchange.getRequestURI(), StandardCharsets.UTF_8);
        double lat = 0;
        double lon = 0;
        String type = "";

        for(var pair : params) {
            if(pair.getName().equals("latitude"))
                lat = Double.parseDouble(pair.getValue());
            else if(pair.getName().equals("longitude"))
                lon = Double.parseDouble(pair.getValue());
            else if(pair.getName().equals("type"))
                type = pair.getValue();
        }

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
