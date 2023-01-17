package londonSafeTravel.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import londonSafeTravel.dbms.document.PointOfInterestDAO;
import londonSafeTravel.schema.document.ConnectionMongoDB;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;

public class POIHandler implements HttpHandler {
    ConnectionMongoDB connection = new ConnectionMongoDB();
    PointOfInterestDAO poi;
     public POIHandler(){
         poi = new PointOfInterestDAO(connection);
     }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

         if(!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        var uriParsed = new URIBuilder(exchange.getRequestURI());

        double latTopLeft = Double.parseDouble(uriParsed.getFirstQueryParam("latTopLeft").getValue());
        double longTopLeft = Double.parseDouble(uriParsed.getFirstQueryParam("longTopLeft").getValue());
        double latBottomRight = Double.parseDouble(uriParsed.getFirstQueryParam("latBottomRight").getValue());
        double longBottomRight = Double.parseDouble(uriParsed.getFirstQueryParam("longBottomRight").getValue());

        var pois = poi.selectPOIsInArea(longTopLeft,longBottomRight,latBottomRight,latTopLeft);
        String json = new Gson().toJson(pois);

        var responseBody = exchange.getResponseBody();

        json.length();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);

        responseBody.write(json.getBytes());
        responseBody.flush();
        responseBody.close();

        exchange.close();
    }
}
