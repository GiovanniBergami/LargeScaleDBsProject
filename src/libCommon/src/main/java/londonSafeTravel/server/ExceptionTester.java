package londonSafeTravel.server;

import com.sun.net.httpserver.HttpExchange;

public class ExceptionTester extends Handler{
    @Override
    public void handleRequest(HttpExchange exchange) throws RuntimeException {
        throw new RuntimeException("I'm sorry Dave, I am afraid that I cannot do that.");
    }
}
