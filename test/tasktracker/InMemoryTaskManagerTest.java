package tasktracker;

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

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private static InMemoryTaskManager taskManager;
    private static Task task1;
    private static Epic epic1;
    private static Epic epic2;
    private static SubTask subTask1;
    private static SubTask subTask2;

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    // Тест-метод в котором мы перед началом каждого теста объявляем объекты и добавляем в менеджер
    @BeforeEach
    void beforeEach() throws IOException {
        super.beforeEach();
        taskManager = createManager();
        task1 = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 12, 0), Duration.ofHours(1));
        epic1 = new Epic("Epic 1", "Описание Epic задачи");
        epic2 = new Epic("Epic 2", "Описание Epic задачи");
        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        subTask1 = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        subTask2 = new SubTask("SubTask 2", "Описание subTask 2", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 14, 0), Duration.ofHours(1));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
    }

    @Test
    void testIsTaskIntersecting() {//Тест-метод по проверки пересечения задач по времени
        SubTask subTask4 = new SubTask("SubTask 3", "Описание subTask 3", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 15, 0), Duration.ofHours(1));
        Assertions.assertFalse(taskManager.isTaskIntersecting(subTask4), "Задачи пересекаются");
        SubTask subTaskTwo = new SubTask(subTask2);
        subTaskTwo.setStartTime(LocalDateTime.of(2024, 3, 29, 12, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            taskManager.updateSubTask(subTaskTwo);
        }, "Ожидалось словить исключение IllegalArgumentException");
        SubTask subTask5 = new SubTask("SubTask 3", "Описание subTask 3", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 12, 50), Duration.ofHours(1));
        Assertions.assertTrue(taskManager.isTaskIntersecting(subTask5), "Задачи не пересекаются");
    }

    //Тест-метод по проверке того, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера
    @Test
    void testTaskIdConflictResolution() {
        Task taskOne = new Task("Задача TaskOne", "Описание задачи TaskOne", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 15, 0), Duration.ofHours(1));
        taskOne.setId(5);
        taskManager.addTask(taskOne);
        Task taskTwo = new Task("Задача TaskTwo", "Описание задачи TaskTwo", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 16, 0), Duration.ofHours(1));
        taskManager.addTask(taskTwo);
        int one = taskOne.getId();
        int two = taskTwo.getId();
        Assertions.assertNotEquals(one, two, "Объекты Task равны");
        Assertions.assertNotNull(taskManager.getTaskId(one), "Такого Task объекта нет в менеджере");
        Assertions.assertNotNull(taskManager.getTaskId(two), "Такого Task объекта нет в менеджере");
    }

    @Test
    void testTaskImmutabilityOnAdd() { //Тест метод по проверки неизменности полей при добавлении в менеджер задач
        Task task = new Task("Задача TaskOne", "Описание задачи TaskOne", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 18, 0), Duration.ofHours(1));
        String name = task.getTask();
        String detalis = task.getDetails();
        Status status = task.getStatus();
        taskManager.addTask(task);
        Task task2 = taskManager.getTaskId(task.getId());
        Assertions.assertEquals(name, task2.getTask(), "Сохраняются разные наименования задач в менеджере");
        Assertions.assertEquals(detalis, task2.getDetails(), "Сохраняются разное описания задач в менеджере");
        Assertions.assertEquals(status, task2.getStatus(), "Сохраняются разный статус задач в менеджере");
    }
}