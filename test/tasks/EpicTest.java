package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskTracker.Managers;
import taskTracker.TaskManager;

class EpicTest {

    private static TaskManager taskManager;
    private static Epic epic1;
    private static Epic epic2;

    @BeforeEach
    void beforeEach() { // Тест-метод в котором мы перед началом каждого теста объявляем объекты и добавляем в менеджер
        taskManager = Managers.getDefault();
        epic1 = new Epic("Задача 1", "Описание задачи 1");
        taskManager.addEpic(epic1);
        epic2 = new Epic("Задача 2", "Описание задачи 2");
        taskManager.addTask(epic2);
    }

    @Test
    void testTaskInheritanceEqualityById() { // Тест-метод в котором объекты Epic с одинаковым id равны
        epic2.setId(epic1.getId());
        epic2.setStatus(Status.IN_PROGRESS);
        Assertions.assertEquals(epic1, epic2, "Объекты не равны друг другу");
    }
    /* У меня изначально нельзя добавлять эпики в подзадачи, Также принцип полиморфизма не действует, так как оба
    объекта наследуются от Task, либо я что-то не так понял. SubTask также нельзя добавить.
     */
}