package londonSafeTravel.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

class QueryPointHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        String test = "Hello World!";
        exchange.sendResponseHeaders(200, test.length());
        exchange.getResponseBody().write(test.getBytes());
        exchange.close();
    }
}
