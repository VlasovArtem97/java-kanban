package taskTracker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    // Тест-метод проверки того, что экземпляры класса TaskManager проинициализированны
    @Test
    void testManagersReturnsInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertNotNull(taskManager, "Объект не инициализирован");
    }

    // Тест-метод проверки того, что экземпляры класса HistoryManager проинициализированны
    @Test
    void testManagersReturnsInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager, "Объект не инициализирован");
    }
}