package tasktracker;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class InMemoryHistoryManagerTest {
    private static Task task1;
    private static Task task2;
    private static Task task3;
    private static Task task4;
    private static Task task5;
    private static Task task6;
    private static HistoryManager historyManager;
    private static TaskManager taskManager;

    @BeforeEach
    void beforeEach() throws IOException, ManagerSaveException {  //Тест-метод в котором мы перед началом каждого теста инициализируем объекты
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager();
        task1 = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 12, 0), Duration.ofHours(1));
        task2 = new Epic("Задача Epic 1", "Описание задачи Epic 1");
        task3 = new Epic("Задача Epic 2", "Описание задачи Epic 2");
        taskManager.addTask(task1);
        taskManager.addEpic((Epic) task2);
        taskManager.addEpic((Epic) task3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        task4 = new SubTask("SubTask 1", "Описание subTask 1", task2.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        task5 = new SubTask("SubTask 2", "Описание subTask 2", task2.getId(),
                LocalDateTime.of(2024, 3, 29, 14, 0), Duration.ofHours(1));
        task6 = new SubTask("SubTask 3", "Описание subTask 3", task2.getId(),
                LocalDateTime.of(2024, 3, 29, 15, 0), Duration.ofHours(1));
        taskManager.addSubTask((SubTask) task4);
        taskManager.addSubTask((SubTask) task5);
        taskManager.addSubTask((SubTask) task6);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
    }

    @Test
    void testTaskAddedToHistory() { // Тест-метод по добавлению задачи в историю
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history, "История просмотров пустая");
        Assertions.assertEquals(6, history.size(), "Размер истории просмотров должен быть равен 6");
        Assertions.assertNotEquals(history.getFirst(), history.getLast(), "В истории просмотров задачи " +
                "2 экземпляра одной и той же задачи");
    }

    @Test
    void testDeletionById() { //Тест-метод по удалению задачи по id из истории просмотров
        List<Task> history1 = historyManager.getHistory();
        Assertions.assertEquals(6, history1.size(), "Размер истории просмотров должен быть равен 6");
        historyManager.remove(task2.getId());
        List<Task> history2 = historyManager.getHistory();
        Assertions.assertEquals(5, history2.size(), "Размер истории просмотров должен быть равен 5");
    }

    @Test
    void testCheckEmptyAndDuplicateAndRemoveTaskHistory() {
        HistoryManager historyManager1 = new InMemoryHistoryManager();
        Assertions.assertTrue(historyManager1.getHistory().isEmpty(), "История задач не пустая");
        historyManager1.add(task1);
        historyManager1.add(task1);
        Assertions.assertEquals(1, historyManager1.getHistory().size(), "В историю просмотров " +
                "добавился дубликат");
        historyManager1.add(task2);
        historyManager1.add(task3);
        historyManager1.add(task4);
        historyManager1.add(task5);
        historyManager1.remove(task1.getId());
        Assertions.assertEquals(4, historyManager1.getHistory().size(), "Из истории просмотров " +
                "не удалилась первая задача");
        historyManager1.remove(task5.getId());
        Assertions.assertEquals(3, historyManager1.getHistory().size(), "Из истории просмотров " +
                "не удалилась последняя задача");
        historyManager1.remove(task3.getId());
        Assertions.assertEquals(2, historyManager1.getHistory().size(), "Из истории просмотров " +
                "не удалилась в середине задача");
    }
}