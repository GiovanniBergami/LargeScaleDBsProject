package londonSafeTravel.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import londonSafeTravel.dbms.document.ConnectionMongoDB;
import londonSafeTravel.dbms.document.PointOfInterestDAO;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;

public class SearchHandler implements HttpHandler {

    ConnectionMongoDB connection;
    PointOfInterestDAO poi;

    public SearchHandler(ConnectionMongoDB connection){
        this.connection = connection;
        poi = new PointOfInterestDAO(connection);
    }


    @Override
    public void handle(HttpExchange exchange) throws RuntimeException, IOException {
        if(!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }
        var uriParsed = new URIBuilder(exchange.getRequestURI());

        String name = uriParsed.getFirstQueryParam("name").getValue();

        var res = poi.findPlace(name);
        /*
            {
               lat:
               long:
             }
       */
        // DA RIVEDERE UN ATTIMO
        String json = new Gson().toJson(res);

        var responseBody = exchange.getResponseBody();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);

        responseBody.write(json.getBytes());
        responseBody.flush();
        responseBody.close();

        exchange.close();
    }
}
