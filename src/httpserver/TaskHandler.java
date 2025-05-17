package httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasks.Task;
import tasktracker.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager managers;

    public TaskHandler(TaskManager managers) {
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metod = exchange.getRequestMethod();
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        switch (metod) {
            case "GET":
                requestGet(pathParts, exchange);
                break;
            case "POST":
                requestPost(body, pathParts, exchange);
                break;
            case "DELETE":
                requestDelete(pathParts, exchange);
                break;
            default:
                sendMethodNotAllowed("Выбранный метод недопустим", exchange);
        }
    }

    @Override
    protected void requestGet(String[] pathParts, HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            try {
                Task task = managers.getTaskId(Integer.parseInt(pathParts[2]));
                if (Objects.isNull(task)) {
                    sendNotFound("Task задача с указанным id - " + pathParts[2] + " не найдена(Not Found)",
                            exchange,
                            404);
                }
                if (Integer.parseInt(pathParts[2]) > 0) {
                    sendText(gson.toJson(task),
                            exchange, 200);
                }
            } catch (NumberFormatException e) {
                sendNotFound("Ошибка: id Task задачи должен быть числом (Bad Request)",
                        exchange, 400);
            }
        } else if (pathParts.length == 2) {
            List<Task> task = managers.getListTask();
            if (task.isEmpty()) {
                sendNotFound("Task задача отсутствуют (Not Found)",
                        exchange,
                        404);
            } else {
                sendText(gson.toJson(managers.getListTask()), exchange, 200);
            }
        } else {
            sendNotFound("Ошибка: проверьте Url запроса (Bad Request)", exchange, 400);
        }
    }

    @Override
    protected void requestPost(String body, String[] pathParts, HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            try {
                Task task = gson.fromJson(body, Task.class);
                Task task1 = managers.getTaskId(Integer.parseInt(pathParts[2]));
                if (Objects.isNull(task)) {
                    sendNotFound("Ошибка: Задача, которую вы пытаетесь обновить, некорректно передана. " +
                                    "Пожалуйста, проверьте формат данных((Not Found)",
                            exchange,
                            404);
                }
                if (Objects.isNull(task1)) {
                    sendNotFound("Task задача с указанным id - " + pathParts[2] + " не найдена(Not Found)",
                            exchange,
                            404);
                }
                if (Integer.parseInt(pathParts[2]) > 0) {
                    if (task.getId() == 0) {
                        sendNotFound("Ошибка: Task задача, которую вы пытаетесь обновить должна иметь id" +
                                " (Bad Request)", exchange, 400);
                    } else {
                        managers.updateTask(task);
                        sendText("Task задача с указанным id - " + pathParts[2] + " успешно обновлена",
                                exchange, 201);
                    }
                }
            } catch (NumberFormatException e) {
                sendNotFound("Ошибка: id Task задачи должен быть числом (Bad Request)", exchange, 400);
            } catch (IllegalArgumentException e) {
                sendHasInteractions("Ошибка: Task задача c id - " + pathParts[2] + " пересекается с другой " +
                        "задачей по времени выполнения (Not Acceptable)", exchange);
            }
        } else if (pathParts.length == 2) {
            try {
                Task task = gson.fromJson(body, Task.class);
                if (task.getId() != 0) {
                    sendNotFound("Ошибка: Task задача, которую вы пытаетесь добавить не должна иметь id" +
                            " (Bad Request)", exchange, 400);
                } else {
                    managers.addTask(task);
                    sendText("Task задача с указанным успешно добавлена",
                            exchange, 201);
                }
            } catch (IllegalArgumentException e) {
                sendHasInteractions("Ошибка: Task задача пересекается с другой " +
                        "задачей по времени выполнения (Not Acceptable)", exchange);
            }
        } else {
            sendNotFound("Ошибка: проверьте Url запроса (Bad Request)", exchange, 400);
        }
    }

    @Override
    protected void requestDelete(String[] pathParts, HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            try {
                Task task = managers.getTaskId(Integer.parseInt(pathParts[2]));
                if (Objects.isNull(task)) {
                    sendNotFound("Task задача с указанным id - " + pathParts[2] + " не найдена(Not Found)",
                            exchange,
                            404);
                }
                if (Integer.parseInt(pathParts[2]) > 0) {
                    managers.deletedTaskById(task.getId());
                    sendText("Task задача успешно удалена", exchange, 200);
                }
            } catch (NumberFormatException e) {
                sendNotFound("Ошибка: id Task задачи должен быть числом", exchange, 400);
            }
        } else {
            sendNotFound("Ошибка: проверьте Url запроса (Bad Request)", exchange, 400);
        }
    }
}
