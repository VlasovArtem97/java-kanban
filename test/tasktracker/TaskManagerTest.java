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
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected static Task task1;
    protected static Epic epic1;
    protected static SubTask subTask1;

    protected abstract T createManager();

    @BeforeEach
    void beforeEach() throws IOException { // Тест-метод в котором мы перед началом каждого теста объявляем объекты и добавляем в менеджер
        taskManager = createManager();
        task1 = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW,
                LocalDateTime.of(2024, 3, 29, 12, 0), Duration.ofHours(1));
        epic1 = new Epic("Epic 1", "Описание Epic задачи");
        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        subTask1 = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
        taskManager.addSubTask(subTask1);
    }

    @Test
    void testGetTasksReturnsListOfTasks() { //Тест метод возвращает список Task задач
        List<Task> tasks = taskManager.getListTask();
        Assertions.assertNotNull(tasks, "Список Task задач пустой");
        Assertions.assertEquals(1, tasks.size(), "Список Task задач не равен 1");
    }

    @Test
    void testGetTasksReturnsListOfEpics() { //Тест метод возвращает список Epic задач
        List<Epic> tasks = taskManager.getListEpic();
        Assertions.assertNotNull(tasks, "Список Epic задач пустой");
        Assertions.assertEquals(1, tasks.size(), "Список Epic задач не равен 1");
    }

    @Test
    void testGetTasksReturnsListOfSubTasks() { //Тест метод возвращает список SubTask задач
        List<SubTask> tasks = taskManager.getListSubTask();
        Assertions.assertNotNull(tasks, "Список subTask задач пустой");
        Assertions.assertEquals(1, tasks.size(), "Список subTask задач не равен 1");
    }

    @Test
    void testDeleteTaskRemovesSpecifiedTask() { //Тест метод по удалению Task задач
        taskManager.deleteAllTask();
        Task tasks = taskManager.getTaskId(1);
        Assertions.assertNull(tasks, "Список Task задач не очистился");
    }

    @Test
    void testDeleteEpicTaskRemovesSpecifiedEpic() { //Тест метод по удалению Epic задач
        taskManager.deleteAllEpic();
        Task tasks = taskManager.getEpicId(2);
        Assertions.assertNull(tasks, "Список Task задач не очистился");
    }

    @Test
    void testDeleteSubtaskTaskRemovesSpecifiedSubtask() { //Тест метод по удалению SubTask задач
        taskManager.deleteAllSubTask();
        Task tasks = taskManager.getSubTaskId(3);
        Assertions.assertNull(tasks, "Список Task задач не очистился");
    }

    @Test
    void testInMemoryTaskManagerAddsAndFindsTasksById() { // Тест-метод по получению Task задачи по id
        Task task = taskManager.getTaskId(task1.getId());
        Assertions.assertNotNull(task, "Task задача не добавлена в Manager");
        Assertions.assertEquals(task1, task, "Задача не добавлена в менеджер и ее нельзя найти по id");
    }

    @Test
    void testInMemoryTaskManagerAddsAndFindsEpicsById() { // Тест-метод по получению Epic задачи по id
        Task epic = taskManager.getEpicId(epic1.getId());
        Assertions.assertNotNull(epic, "Epic задача не добавлена в Manager");
    }

    @Test
    void testInMemoryTaskManagerAddsAndFindsSubtasksById() { // Тест-метод по получению SubTask задачи по id
        Task subTask = taskManager.getSubTaskId(subTask1.getId());
        Assertions.assertNotNull(subTask, "SubTask задача не добавлена в Manager");
    }

    @Test
    void testAddTask() {
        Assertions.assertEquals(1, taskManager.getListTask().size(), "Метод не добавляет " +
                "Task задачу в список");
    }

    @Test
    void testAddEpic() {
        Assertions.assertEquals(1, taskManager.getListEpic().size(), "Метод не добавляет " +
                "Epic задачу в список");
    }

    @Test
    void testAddSubTask() {
        Assertions.assertEquals(1, taskManager.getListSubTask().size(), "Метод не добавляет " +
                "SubTask задачу в список");
    }

    @Test
    void testUpdateTask() {
        Task taskOne = new Task(task1);
        taskOne.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(taskOne);
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getTaskId(1).getStatus(),
                "Метод по обновлению Task задачи не работает");
    }

    @Test
    void testUpdateEpic() {
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getEpicId(2).getStatus(),
                "Метод по обновлению Epic задачи не работает");
    }

    @Test
    void testUpdateSubTask() {
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getSubTaskId(3).getStatus(),
                "Метод по обновлению subTask задачи не работает");
    }

    @Test
    void testDeletedTaskById() { // Тест метод по удалению Task по id
        taskManager.deletedTaskById(1);
        Task task = taskManager.getTaskId(1);
        Assertions.assertNull(task, "Task не удаляется по id");
    }

    @Test
    void testDeletedEpicById() { // Тест метод по удалению Epic по id
        taskManager.deletedEpicById(2);
        Task task = taskManager.getEpicId(2);
        Assertions.assertNull(task, "Epic не удаляется по id");
    }

    @Test
    void testDeletedSubTaskById() { // Тест метод по удалению SubTask по id
        taskManager.deletedSubTaskById(3);
        Task task = taskManager.getSubTaskId(3);
        Assertions.assertNull(task, "Task не удаляется по id");
    }

    @Test
    void testGetAllSubTaskByEpic() { //Тест метод по получению Subtasks из Epic
        List<SubTask> subTasks = epic1.getSubTasks();
        Assertions.assertFalse(subTasks.isEmpty(), "Список пуст");
    }
}