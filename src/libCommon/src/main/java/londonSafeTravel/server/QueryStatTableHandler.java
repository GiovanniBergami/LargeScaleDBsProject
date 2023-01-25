package londonSafeTravel.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import londonSafeTravel.dbms.document.ConnectionMongoDB;
import londonSafeTravel.dbms.document.DisruptionStatsDAO;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;

public class QueryStatTableHandler implements HttpHandler{
    DisruptionStatsDAO tableDis;
    public QueryStatTableHandler(ConnectionMongoDB connenction){
        tableDis = new DisruptionStatsDAO(connenction);
    }

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

        var ress = tableDis.commonDisruptionInArea(longTopLeft,longBottomRight,latBottomRight,latTopLeft);
        StringBuilder jsonBuilder = new StringBuilder("[");
        for(var doc : ress){
            jsonBuilder.append(doc.toJson()).append(",");
        }
        if(jsonBuilder.length() > 1)
            jsonBuilder.delete(jsonBuilder.length() - 1, jsonBuilder.length());

        jsonBuilder.append("]");
        String json = jsonBuilder.toString();

        var responseBody = exchange.getResponseBody();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);

        responseBody.write(json.getBytes());
        responseBody.flush();
        responseBody.close();

        exchange.close();
    }



}
