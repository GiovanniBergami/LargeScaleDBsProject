package londonSafeTravel.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import londonSafeTravel.schema.document.DisruptionDAO;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;

public class HeatmapHandler implements HttpHandler {
    DisruptionDAO heatmapDAO;
    public HeatmapHandler() {
        heatmapDAO = new DisruptionDAO();
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var uriParsed = new URIBuilder(exchange.getRequestURI());
        long lenLat = Long.parseLong(uriParsed.getFirstQueryParam("lenLat").getValue());
        long lenLon = Long.parseLong(uriParsed.getFirstQueryParam("lenLon").getValue());
        // @TODO sort this crap out
        StringBuilder jsonBuilder = new StringBuilder("[");

        for(var heatcell : heatmapDAO.queryHeatmap(
                lenLat/1000.0, lenLon/1000.0, uriParsed.getFirstQueryParam("class").getValue())) {
            jsonBuilder.append(heatcell.toJson()).append(",");
        }

        if(jsonBuilder.length() > 1)
            jsonBuilder.delete(jsonBuilder.length() - 1, jsonBuilder.length());

        jsonBuilder.append("]");

        String json = jsonBuilder.toString();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);

        exchange.getResponseBody().write(json.getBytes());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }
}
