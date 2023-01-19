package londonSafeTravel.server;

import com.sun.net.httpserver.HttpExchange;
import londonSafeTravel.gsonUtils.GsonFactory;
import londonSafeTravel.dbms.document.PointOfInterestDAO;
import londonSafeTravel.dbms.document.ConnectionMongoDB;
import org.apache.hc.core5.net.URIBuilder;

public class POIHandler extends Handler {
    ConnectionMongoDB connection;
    PointOfInterestDAO poi;
     public POIHandler(){
         this(new ConnectionMongoDB());
     }

     public POIHandler(ConnectionMongoDB connection) {
         this.connection = connection;
         poi = new PointOfInterestDAO(connection);
     }


    @Override
    public void handleRequest(HttpExchange exchange) throws Exception {

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
        String json = GsonFactory.build().toJson(pois);

        var responseBody = exchange.getResponseBody();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);

        responseBody.write(json.getBytes());
        responseBody.flush();
        responseBody.close();

    }
}
