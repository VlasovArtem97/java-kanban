package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.ManagerSaveException;
import tasktracker.Managers;
import tasktracker.TaskManager;

import java.io.IOException;

class SubTaskTest {

    private static TaskManager taskManager;
    private static Epic epic;
    private static SubTask subTask1;
    private static SubTask subTask2;

    @BeforeEach
    void beforeEach() throws IOException, ManagerSaveException { // Тест-метод в котором мы перед началом каждого теста объявляем объекты и добавляем в менеджер
        taskManager = Managers.getDefault();
        epic = new Epic("Задача Эпик1", "Описание эпик задачи");
        taskManager.addEpic(epic);
        subTask1 = new SubTask("Задача 1", "Описание задачи 1", epic.getId());
        taskManager.addSubTask(subTask1);
        subTask2 = new SubTask("Задача 2", "Описание задачи 2", epic.getId());
        taskManager.addSubTask(subTask2);
    }

    @Test
    void testTaskInheritanceEqualityById() { // Тест-метод в котором объекты SubTask с одинаковым id равны
        subTask2.setId(subTask1.getId());
        subTask2.setStatus(Status.IN_PROGRESS);
        Assertions.assertEquals(subTask1, subTask2, "Объекты не равны друг другу");
    }
}