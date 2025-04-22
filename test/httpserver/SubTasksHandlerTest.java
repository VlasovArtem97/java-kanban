package httpserver;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasktracker.Managers;
import tasktracker.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubTasksHandlerTest {
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private Gson gson;
    private Epic epic1;

    @BeforeEach
    public void setUp() throws IOException {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .setPrettyPrinting()
                .create();
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();
        taskManager.deleteAllSubTask();
        epic1 = new Epic("Epic 1", "Описание 1 Epic задачи");
        taskManager.addEpic(epic1);
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        // создаём задачу
        SubTask task = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json;charset=utf-8")
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> tasksFromManager = taskManager.getListSubTask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("SubTask 1", tasksFromManager.get(0).getTask(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        // создаём задачу
        SubTask task = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        // конвертируем её в JSON
        taskManager.addSubTask(task);
        SubTask task1 = new SubTask(task);
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(task1);
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json;charset=utf-8")
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> tasksFromManager = taskManager.getListSubTask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(Status.IN_PROGRESS, tasksFromManager.get(0).getStatus(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubTask() throws IOException, InterruptedException {
        SubTask task = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        SubTask task2 = new SubTask("SubTask 2", "Описание subTask 2", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 14, 0), Duration.ofHours(1));
        taskManager.addSubTask(task);
        taskManager.addSubTask(task2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json;charset=utf-8")
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "Преполагалось, что будет Gson с массивом объектов");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(2, jsonArray.size(), "Предполагалась наличия 2-х задач");
        List<SubTask> tasks = gson.fromJson(jsonArray, new TypeToken<List<SubTask>>() {
        }.getType());
        assertEquals(task, tasks.get(0), "Предполагалась равенство Task задач");
    }

    @Test
    public void testGetSubTaskId() throws IOException, InterruptedException {
        SubTask task = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        SubTask task2 = new SubTask("SubTask 2", "Описание subTask 2", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 14, 0), Duration.ofHours(1));
        taskManager.addSubTask(task);
        taskManager.addSubTask(task2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json;charset=utf-8")
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject(), "Преполагалось, что будет Gson объект");
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        SubTask oldTask = gson.fromJson(jsonObject, SubTask.class);
        assertEquals(task, oldTask, "Предполагалась равенство Task задач");
    }

    @Test
    public void testDeleteSubTaskById() throws IOException, InterruptedException {
        SubTask task = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        SubTask task2 = new SubTask("SubTask 2", "Описание subTask 2", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 14, 0), Duration.ofHours(1));
        taskManager.addSubTask(task);
        taskManager.addSubTask(task2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .header("Content-Type", "application/json;charset=utf-8")
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> tasksFromManager = taskManager.getListSubTask();
        assertEquals(1, tasksFromManager.size(), "Предполагалось 1 Task задача");
    }
}