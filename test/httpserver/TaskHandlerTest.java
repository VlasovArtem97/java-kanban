package httpserver;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;
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

class TaskHandlerTest {

    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private Gson gson;

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
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 12, 0), Duration.ofHours(1));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
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
        List<Task> tasksFromManager = taskManager.getListTask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача Task 1", tasksFromManager.get(0).getTask(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 12, 0), Duration.ofHours(1));
        // конвертируем её в JSON
        taskManager.addTask(task);
        Task task1 = new Task(task);
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        String taskJson = gson.toJson(task1);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
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
        List<Task> tasksFromManager = taskManager.getListTask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(Status.IN_PROGRESS, tasksFromManager.get(0).getStatus(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 12, 0), Duration.ofHours(1));
        Task task2 = new Task("Задача Task 2", "Описание задачи Task 2", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 11, 0), Duration.ofHours(1));
        taskManager.addTask(task);
        taskManager.addTask(task2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
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
        List<Task> tasks = gson.fromJson(jsonArray, new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(task, tasks.get(0), "Предполагалась равенство Task задач");
    }

    @Test
    public void testGetTaskId() throws IOException, InterruptedException {
        Task task = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 12, 0), Duration.ofHours(1));
        Task task2 = new Task("Задача Task 2", "Описание задачи Task 2", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 11, 0), Duration.ofHours(1));
        taskManager.addTask(task);
        taskManager.addTask(task2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
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
        Task oldTask = gson.fromJson(jsonObject, Task.class);
        assertEquals(task, oldTask, "Предполагалась равенство Task задач");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 12, 0), Duration.ofHours(1));
        Task task2 = new Task("Задача Task 2", "Описание задачи Task 2", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 11, 0), Duration.ofHours(1));
        taskManager.addTask(task);
        taskManager.addTask(task2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
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
        List<Task> tasksFromManager = taskManager.getListTask();
        assertEquals(1, tasksFromManager.size(), "Предполагалось 1 Task задача");
    }
}
