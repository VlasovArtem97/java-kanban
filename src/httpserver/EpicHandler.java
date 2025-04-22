package httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasks.Epic;
import tasks.SubTask;
import tasktracker.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager managers;

    public EpicHandler(TaskManager managers) {
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metod = exchange.getRequestMethod();
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        switch (metod) {
            case "GET":
                getEpicsHandle(pathParts, exchange);
                break;
            case "POST":
                postEpicsHandle(body, pathParts, exchange);
                break;
            case "DELETE":
                deleteEpicsHandle(pathParts, exchange);
                break;
            default:
                sendNotFound("Выбранный метод недопустим", exchange, 405);
        }
    }

    private void getEpicsHandle(String[] pathParts, HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            try {
                Epic task = managers.getEpicId(Integer.parseInt(pathParts[2]));
                if (Objects.isNull(task)) {
                    sendNotFound("Epic задача с указанным id - " + pathParts[2] + " не найдена(Not Found)",
                            exchange,
                            404);
                }
                if (Integer.parseInt(pathParts[2]) > 0) {
                    sendText(gson.toJson(task),
                            exchange, 200);
                }
            } catch (NumberFormatException e) {
                sendNotFound("Ошибка: id Epic задачи должен быть числом (Bad Request)",
                        exchange, 400);
            }
        } else if (pathParts.length == 2) {
            List<Epic> epics = managers.getListEpic();
            if (epics.isEmpty()) {
                sendNotFound("Epic задачи отсутствуют (Not Found)",
                        exchange,
                        404);
            } else {
                sendText(gson.toJson(epics), exchange, 200);
            }
        } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
            try {
                Epic task = managers.getEpicId(Integer.parseInt(pathParts[2]));
                List<SubTask> subTasks = managers.allSubTaskByEpic(Integer.parseInt(pathParts[2]));
                if (Objects.isNull(task)) {
                    sendNotFound("Epic отсутствует с указанным id - " + pathParts[2] + " (Not Found)",
                            exchange,
                            404);
                }
                if (subTasks.isEmpty()) {
                    sendNotFound("Epic задачи отсутствуют с указанным Epic id - " + pathParts[2] + " (Not Found)",
                            exchange,
                            404);
                }
                if (Integer.parseInt(pathParts[2]) > 0) {
                    sendText(gson.toJson(managers.allSubTaskByEpic(Integer.parseInt(pathParts[2]))),
                            exchange, 200);
                }
            } catch (NumberFormatException e) {
                sendNotFound("Ошибка: id Epic задачи должен быть числом", exchange, 400);
            }
        } else {
            sendNotFound("Ошибка: проверьте Url запроса (Bad Request)", exchange, 400);
        }
    }

    private void postEpicsHandle(String body, String[] pathParts, HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            try {
                Epic task = gson.fromJson(body, Epic.class);
                Epic task1 = managers.getEpicId(Integer.parseInt(pathParts[2]));
                if (Objects.isNull(task)) {
                    sendNotFound("Ошибка: Задача, которую вы пытаетесь обновить, некорректно передана. " +
                                    "Пожалуйста, проверьте формат данных((Not Found)",
                            exchange,
                            404);
                }
                if (Objects.isNull(task1)) {
                    sendNotFound("Epic задача с указанным id - " + pathParts[2] + " не найдена(Not Found)",
                            exchange,
                            404);
                }
                if (Integer.parseInt(pathParts[2]) > 0) {
                    if (task.getId() == 0) {
                        sendNotFound("Ошибка: Epic задача, которую вы пытаетесь обновить должна иметь id" +
                                " (Bad Request)", exchange, 400);
                    } else {
                        managers.updateEpic(task);
                        sendText("Epic задача с указанным id - " + pathParts[2] + " успешно обновлена",
                                exchange, 201);
                    }
                }
            } catch (NumberFormatException e) {
                sendNotFound("Ошибка: id Epic задачи должен быть числом (Bad Request)", exchange, 400);
            } catch (IllegalArgumentException e) {
                sendHasInteractions("Ошибка: Epic задача c id - " + pathParts[2] + " пересекается с другой " +
                        "задачей по времени выполнения (Not Acceptable)", exchange, 406);
            }
        } else if (pathParts.length == 2) {
            try {
                Epic task = gson.fromJson(body, Epic.class);
                if (task.getId() != 0) {
                    sendNotFound("Ошибка: Epic задача, которую вы пытаетесь добавить не должна иметь id" +
                            " (Bad Request)", exchange, 400);
                } else {
                    managers.addEpic(task);
                    sendText("Epic задача успешно добавлена",
                            exchange, 201);
                }
            } catch (IllegalArgumentException e) {
                sendHasInteractions("Ошибка: Epic задача пересекается с другой " +
                        "задачей по времени выполнения (Not Acceptable)", exchange, 406);
            }
        } else {
            sendNotFound("Ошибка: проверьте Url запроса (Bad Request)", exchange, 400);
        }
    }

    private void deleteEpicsHandle(String[] pathParts, HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            try {
                Epic task = managers.getEpicId(Integer.parseInt(pathParts[2]));
                if (Objects.isNull(task)) {
                    sendNotFound("Epic задача с указанным id - " + pathParts[2] + " не найдена(Not Found)",
                            exchange,
                            404);
                }
                if (Integer.parseInt(pathParts[2]) > 0) {
                    managers.deletedEpicById(task.getId());
                    sendText("Epic задача успешно удалена", exchange, 200);
                }
            } catch (NumberFormatException e) {
                sendNotFound("Ошибка: id Epic задачи должен быть числом", exchange, 400);
            }
        } else {
            sendNotFound("Ошибка: проверьте Url запроса (Bad Request)", exchange, 400);
        }
    }
}
