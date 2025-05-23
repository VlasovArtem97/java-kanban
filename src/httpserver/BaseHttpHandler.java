package httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import servertypeadapters.DurationTypeAdapter;
import servertypeadapters.LocalTimeTypeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {

    protected Gson gson;

    public BaseHttpHandler() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .setPrettyPrinting()
                .create();
    }

    protected void sendText(String text, HttpExchange exchange, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendNotFound(String text, HttpExchange exchange, int statusCode) throws IOException {
        sendText(text, exchange, statusCode);
    }

    protected void sendHasInteractions(String text, HttpExchange exchange) throws IOException {
        sendText(text, exchange, 406);
    }

    protected void sendMethodNotAllowed(String text, HttpExchange exchange) throws IOException {
        sendText(text, exchange, 405);
    }

    protected void requestGet(String[] pathParts, HttpExchange exchange) throws IOException {
        sendMethodNotAllowed("Метод отправки не разрешен", exchange);
    }

    protected void requestPost(String body, String[] pathParts, HttpExchange exchange) throws IOException {
        sendMethodNotAllowed("Метод отправки не разрешен", exchange);
    }

    protected void requestDelete(String[] pathParts, HttpExchange exchange) throws IOException {
        sendMethodNotAllowed("Метод отправки не разрешен", exchange);
    }
}
