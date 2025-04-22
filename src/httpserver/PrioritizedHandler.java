package httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasks.Task;
import tasktracker.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager managers;

    public PrioritizedHandler(TaskManager managers) {
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metod = exchange.getRequestMethod();
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        switch (metod) {
            case "GET":
                if (pathParts.length == 2) {
                    List<Task> tasks = managers.getPrioritizedTasks();
                    if (tasks.isEmpty()) {
                        sendNotFound("Список приоритетных задач не создан (Not Found)",
                                exchange,
                                404);
                    } else {
                        sendText(gson.toJson(tasks), exchange, 200);
                    }
                } else {
                    sendNotFound("Ошибка: проверьте Url запроса (Bad Request)", exchange, 400);
                }
                break;
            default:
                sendNotFound("Выбранный метод недопустим", exchange, 405);
        }
    }
}
