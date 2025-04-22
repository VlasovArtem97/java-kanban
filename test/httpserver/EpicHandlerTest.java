package httpserver;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
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

class EpicHandlerTest {
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
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Epic 2", "Описание Epic 2 задачи");
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
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
        List<Epic> tasksFromManager = taskManager.getListEpic();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic 1", tasksFromManager.get(0).getTask(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic task = new Epic("Epic 2", "Описание Epic 2 задачи");
        taskManager.addEpic(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
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
        List<Epic> tasks = gson.fromJson(jsonArray, new TypeToken<List<Epic>>() {
        }.getType());
        assertEquals(task, tasks.get(1), "Предполагалась равенство Task задач");
    }

    @Test
    public void testGetEpicId() throws IOException, InterruptedException {
        Epic task = new Epic("Epic 2", "Описание Epic 2 задачи");
        taskManager.addEpic(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
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
        Epic oldTask = gson.fromJson(jsonObject, Epic.class);
        assertEquals(task, oldTask, "Предполагалась равенство Epic задач");
    }

    @Test
    public void testGetSubtaskByEpicId() throws IOException, InterruptedException {
        SubTask task = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        SubTask task2 = new SubTask("SubTask 2", "Описание subTask 2", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 14, 0), Duration.ofHours(1));
        taskManager.addSubTask(task);
        taskManager.addSubTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
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
        System.out.println(jsonElement);
        assertTrue(jsonElement.isJsonArray(), "Преполагалось, что будет массив Gson элементов");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<SubTask> tasks = gson.fromJson(jsonArray, new TypeToken<List<SubTask>>() {}.getType());
        assertEquals(2, jsonArray.size(), "Предполагалась наличия 2-х SubTask задачи");
        assertEquals(task, tasks.get(0), "Предполагалась равенство SubTask задач");
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic task = new Epic("Epic 2", "Описание Epic 2 задачи");
        taskManager.addEpic(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
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
        List<Epic> tasksFromManager = taskManager.getListEpic();
        assertEquals(1, tasksFromManager.size(), "Предполагалось 1 Epic задача");
    }
}