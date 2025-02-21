package tasktracker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

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
    void beforeEach() {  //Тест-метод в котором мы перед началом каждого теста инициализируем объекты
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager();
        task1 = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW);
        task2 = new Epic("Задача Epic 1", "Описание задачи Epic 1");
        task3 = new Epic("Задача Epic 2", "Описание задачи Epic 2");
        taskManager.addTask(task1);
        taskManager.addEpic((Epic) task2);
        taskManager.addEpic((Epic) task3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        task4 = new SubTask("Задача SubTasks 1", "Описание задачи SubTasks 1", task2.getId());
        task5 = new SubTask("Задача SubTasks 2", "Описание задачи SubTasks 2", task2.getId());
        task6 = new SubTask("Задача SubTasks 3", "Описание задачи SubTasks 3", task2.getId());
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
}