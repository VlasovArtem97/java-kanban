package httpserver;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servertypeadapters.DurationTypeAdapter;
import servertypeadapters.LocalTimeTypeAdapter;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrioritizedHandlerTest {
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
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 12, 0), Duration.ofHours(1));
        Task task2 = new Task("Задача Task 2", "Описание задачи Task 2", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 11, 0), Duration.ofHours(1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        SubTask subTask1 = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        SubTask subTask2 = new SubTask("SubTask 2", "Описание subTask 2", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 14, 0), Duration.ofHours(1));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
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
        assertEquals(4, jsonArray.size(), "Предполагалась наличия 4-х приоритетных задач");
        List<Task> tasks = gson.fromJson(jsonArray, new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(task1, tasks.get(1), "Предполагалась равенство Task задач");
        assertEquals(task2, tasks.get(0), "Предполагалась равенство Task задач");
    }
}