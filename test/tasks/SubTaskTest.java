package tasks;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.Managers;
import tasktracker.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

class SubTaskTest {

    private static TaskManager taskManager;
    private static Epic epic1;
    private static SubTask subTask1;
    private static SubTask subTask2;

    @BeforeEach
    void beforeEach() throws IOException, ManagerSaveException { // Тест-метод в котором мы перед началом каждого теста объявляем объекты и добавляем в менеджер
        taskManager = Managers.getDefault();
        epic1 = new Epic("Задача Эпик1", "Описание эпик задачи");
        taskManager.addEpic(epic1);
        subTask1 = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        subTask2 = new SubTask("SubTask 2", "Описание subTask 2", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 14, 0), Duration.ofHours(1));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
    }

    @Test
    void testTaskInheritanceEqualityById() { // Тест-метод в котором объекты SubTask с одинаковым id равны
        subTask2.setId(subTask1.getId());
        subTask2.setStatus(Status.IN_PROGRESS);
        Assertions.assertEquals(subTask1, subTask2, "Объекты не равны друг другу");
    }
}