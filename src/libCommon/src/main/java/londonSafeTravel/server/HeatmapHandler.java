package londonSafeTravel.server;

import com.sun.net.httpserver.HttpExchange;
import londonSafeTravel.dbms.document.ConnectionMongoDB;
import londonSafeTravel.dbms.document.DisruptionDAO;
import org.apache.hc.core5.net.URIBuilder;

public class HeatmapHandler extends Handler {
    DisruptionDAO heatmapDAO;
    public HeatmapHandler() {
        heatmapDAO = new DisruptionDAO();
    }

    public HeatmapHandler(ConnectionMongoDB connection) {
        heatmapDAO = new DisruptionDAO(connection);
    }

    @Override
    public void handleRequest(HttpExchange exchange) throws Exception {
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
    }
}
