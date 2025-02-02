package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskTracker.Managers;
import taskTracker.TaskManager;

class TaskTest {

    private static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {  //Тест-метод в котором мы перед началом каждого теста инициализируем объект
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldCompareTaskObjectsById() { // Тест метод по сравнению Task объектов
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW);
        taskManager.addTask(task2);
        task2.setId(task1.getId());
        task2.setStatus(Status.IN_PROGRESS);
        Assertions.assertEquals(task1, task2, "Объекты не равны друг другу");
    }
}