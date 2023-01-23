package londonSafeTravel.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class Handler implements HttpHandler {
    public abstract void handleRequest(HttpExchange exchange) throws Exception;

    @Override
    public void handle(HttpExchange exchange) {
        System.out.println(
                this.getClass().getName() + "\tIncoming "
                + exchange.getRequestMethod() + " request with URI " + exchange.getRequestURI());

        try (exchange) { // auto-close resource
            handleRequest(exchange);
        } catch (Exception e) {
            System.err.println(this.getClass().getName() + "\t" + e);
            e.printStackTrace();
        }
    }
}
