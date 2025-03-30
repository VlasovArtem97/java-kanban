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

class EpicTest {

    private static TaskManager taskManager;
    private static Epic epic1;
    private static Epic epic2;
    private static SubTask subTask1;
    private static SubTask subTask2;
    private static SubTask subTask3;

    @BeforeEach
    void beforeEach() throws IOException { // Тест-метод в котором мы перед началом каждого теста объявляем объекты и добавляем в менеджер
        taskManager = Managers.getDefault();
        epic1 = new Epic("Задача 1", "Описание задачи 1");
        epic2 = new Epic("Задача 2", "Описание задачи 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        subTask1 = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        subTask2 = new SubTask("SubTask 2", "Описание subTask 2", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 14, 0), Duration.ofHours(1));
        subTask3 = new SubTask("SubTask 3", "Описание subTask 3", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 15, 0), Duration.ofHours(1));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
    }

    @Test
    void testUpdateTaskStatus() {
        Assertions.assertEquals(Status.NEW, epic1.getStatus(), "Статус Epic задачи не равен NEW");
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);
        Assertions.assertEquals(Status.DONE, epic1.getStatus(), "Статус Epic задачи не изменился на DONE");
        subTask1.setStatus(Status.NEW);
        taskManager.updateSubTask(subTask1);
        Assertions.assertEquals(Status.NEW, epic1.getStatus(), "Статус Epic задачи не изменился" +
                " на NEW");
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);
        subTask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);
        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Статус Epic задачи не изменился" +
                " на IN_PROGRESS");
    }

    @Test
    void testTaskInheritanceEqualityById() { // Тест-метод в котором объекты Epic с одинаковым id равны
        epic2.setId(epic1.getId());
        epic2.setStatus(Status.IN_PROGRESS);
        Assertions.assertEquals(epic1, epic2, "Задачи Epic не равны друг другу");
    }
    /* У меня изначально нельзя добавлять эпики в подзадачи, Также принцип полиморфизма не действует, так как оба
    объекта наследуются от Task, либо я что-то не так понял. SubTask также нельзя добавить.
     */
}