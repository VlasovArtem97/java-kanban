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

class TaskTest {

    private static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {  //Тест-метод в котором мы перед началом каждого теста инициализируем объект
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldCompareTaskObjectsById() throws IOException, ManagerSaveException { // Тест метод по сравнению Task объектов
        Task task1 = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW,
                    LocalDateTime.of(2024,3,29,12,0), Duration.ofHours(1));
        taskManager.addTask(task1);
        Task task2 = new Task("Задача Task 2", "Описание задачи Task 2", Status.NEW,
                LocalDateTime.of(2024,3,29,11,0), Duration.ofHours(1));
        taskManager.addTask(task2);
        task2.setId(task1.getId());
        task2.setStatus(Status.IN_PROGRESS);
        Assertions.assertEquals(task1, task2, "Объекты не равны друг другу");
    }
}