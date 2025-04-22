package httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasks.SubTask;
import tasks.Task;
import tasktracker.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class SubTasksHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager managers;

    public SubTasksHandler(TaskManager managers) {
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metod = exchange.getRequestMethod();
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        switch (metod) {
            case "GET":
                getSubtasksHandle(pathParts, exchange);
                break;
            case "POST":
                postSubtasksHandle(body, pathParts, exchange);
                break;
            case "DELETE":
                deleteSubtasksHandle(pathParts, exchange);
                break;
            default:
                sendNotFound("Выбранный метод недопустим", exchange, 405);
        }
    }

    private void getSubtasksHandle(String[] pathParts, HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            try {
                SubTask task = managers.getSubTaskId(Integer.parseInt(pathParts[2]));
                if (Objects.isNull(task)) {
                    sendNotFound("Subtask задача с указанным id - " + pathParts[2] + " не найдена(Not Found)",
                            exchange,
                            404);
                }
                if (Integer.parseInt(pathParts[2]) > 0) {
                    sendText(gson.toJson(task),
                            exchange, 200);
                }
            } catch (NumberFormatException e) {
                sendNotFound("Ошибка: id Subtask задачи должен быть числом (Bad Request)",
                        exchange, 400);
            }
        } else if (pathParts.length == 2) {
            List<SubTask> subTasks = managers.getListSubTask();
            if (subTasks.isEmpty()) {
                sendNotFound("Subtask задачи отсутствуют (Not Found)",
                        exchange,
                        404);
            } else {
                sendText(gson.toJson(managers.getListSubTask()), exchange, 200);
            }
        } else {
            sendNotFound("Ошибка: проверьте Url запроса (Bad Request)", exchange, 400);
        }
    }

    private void postSubtasksHandle(String body, String[] pathParts, HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            try {
                SubTask task = gson.fromJson(body, SubTask.class);
                SubTask task1 = managers.getSubTaskId(Integer.parseInt(pathParts[2]));
                if (Objects.isNull(task)) {
                    sendNotFound("Ошибка: Задача, которую вы пытаетесь обновить, некорректно передана. " +
                                    "Пожалуйста, проверьте формат данных(Not Found)",
                            exchange,
                            404);
                }
                if (Objects.isNull(task1)) {
                    sendNotFound("SubTask задача с указанным id - " + pathParts[2] + " не найдена(Not Found)",
                            exchange,
                            404);
                }
                if (Integer.parseInt(pathParts[2]) > 0) {
                    if (task.getId() == 0) {
                        sendNotFound("Ошибка: SubTask задача, которую вы пытаетесь обновить должна иметь id" +
                                " (Bad Request)", exchange, 400);
                    } else {
                        managers.updateSubTask(task);
                        sendText("SubTask задача с указанным id - " + pathParts[2] + " успешно обновлена",
                                exchange, 201);
                    }
                }
            } catch (NumberFormatException e) {
                sendNotFound("Ошибка: id Subtask задачи должен быть числом (Bad Request)", exchange, 400);
            } catch (IllegalArgumentException e) {
                sendHasInteractions("Ошибка: Subtasks задача c id - " + pathParts[2] + " пересекается с другой " +
                        "задачей по времени выполнения (Not Acceptable)", exchange, 406);
            }
        } else if (pathParts.length == 2) {
            try {
                SubTask task = gson.fromJson(body, SubTask.class);
                if (task.getId() != 0) {
                    sendNotFound("Ошибка: Subtask задача, которую вы пытаетесь добавить не должна иметь id" +
                            " (Bad Request)", exchange, 400);
                } else {
                    managers.addSubTask(task);
                    sendText("SubTask задача успешно добавлена",
                            exchange, 201);
                }
            } catch (IllegalArgumentException e) {
                sendHasInteractions("Ошибка: Subtask задача пересекается с другой " +
                        "задачей по времени выполнения (Not Acceptable)", exchange, 406);
            }
        } else {
            sendNotFound("Ошибка: проверьте Url запроса (Bad Request)", exchange, 400);
        }
    }

    private void deleteSubtasksHandle(String[] pathParts, HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            try {
                Task task = managers.getSubTaskId(Integer.parseInt(pathParts[2]));
                if (Objects.isNull(task)) {
                    sendNotFound("Subtask задача с указанным id - " + pathParts[2] + " не найдена(Not Found)",
                            exchange,
                            404);
                }
                if (Integer.parseInt(pathParts[2]) > 0 && task != null) {
                    managers.deletedSubTaskById(task.getId());
                    sendText("Subtask задача успешно удалена", exchange, 200);
                }
            } catch (NumberFormatException e) {
                sendNotFound("Ошибка: id Subtask задачи должен быть числом", exchange, 400);
            }
        } else {
            sendNotFound("Ошибка: проверьте Url запроса (Bad Request)", exchange, 400);
        }
    }
}
