package londonSafeTravel.server;

import com.sun.net.httpserver.HttpExchange;
import londonSafeTravel.dbms.document.ConnectionMongoDB;
import londonSafeTravel.dbms.document.LineGraphDAO;
import londonSafeTravel.dbms.document.PointOfInterestDAO;
import londonSafeTravel.gsonUtils.GsonFactory;
import org.apache.hc.core5.net.URIBuilder;

public class LineGraphHandler extends Handler{
    LineGraphDAO lineGraphDAO;

    public LineGraphHandler(ConnectionMongoDB connection) {
        lineGraphDAO = new LineGraphDAO(connection);
    }

    @Override
    public void handleRequest(HttpExchange exchange) throws Exception {
        if(!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        var uriParsed = new URIBuilder(exchange.getRequestURI());
        var subCatMatch = uriParsed.isQueryEmpty() ? null : uriParsed.getFirstQueryParam("category");
        String category = subCatMatch == null ? null : subCatMatch.getValue();

        String json = GsonFactory.build().toJson(lineGraphDAO.computeGraph(category));

        var responseBody = exchange.getResponseBody();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);

        responseBody.write(json.getBytes());
        responseBody.flush();
        responseBody.close();
    }
}
