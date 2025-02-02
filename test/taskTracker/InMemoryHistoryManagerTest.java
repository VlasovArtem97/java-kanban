package taskTracker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

class InMemoryHistoryManagerTest {
    private static Task task1;
    private static final int MAX_SIZE_HISTORY = 10;
    private static List<Task> history;
    private static HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {  //Тест-метод в котором мы перед началом каждого теста инициализируем объекты
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        history = new ArrayList<>();
    }

    @Test
    void testTaskAddedToHistory() { // Тест-метод по добавлению задачи в историю, в соответствии с ее размером
        for (int i = 0; i < 12; i++) {
            task1 = new Task("Задача", "Описание задачи", Status.NEW);
            historyManager.add(task1);
        }
        history = historyManager.getHistory();
        Assertions.assertNotNull(history, "История не пустая.");
        Assertions.assertEquals(MAX_SIZE_HISTORY, history.size(), "Размер не соответствует требованиям");
    }
}