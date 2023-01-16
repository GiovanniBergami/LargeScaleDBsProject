package londonSafeTravel.server;

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
        var uriParsed = new URIBuilder(exchange.getRequestURI());

        // Continuare prendendo i paramet

         //var pois = poi.query1();
    }
}
