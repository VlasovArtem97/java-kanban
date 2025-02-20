package taskTracker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

class InMemoryTaskManagerTest {

    private static InMemoryTaskManager taskManager;
    private static Task task1;
    private static Epic epic1;
    private static SubTask subTask1;

    @BeforeEach
    void beforeEach() { // Тест-метод в котором мы перед началом каждого теста объявляем объекты и добавляем в менеджер
        taskManager = new InMemoryTaskManager();
        task1 = new Task("Task 1", "Описание Task задачи", Status.NEW);
        epic1 = new Epic("Epic 1", "Описание Epic задачи");
        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        subTask1 = new SubTask("SubTask 1", "Описание SubTask задачи", epic1.getId());
        taskManager.addSubTask(subTask1);
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

    //Тест-метод по проверке того, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера
    @Test
    void testTaskIdConflictResolution() {
        Task taskOne = new Task("Task 1", "описание TaskOne", Status.NEW);
        taskOne.setId(5);
        taskManager.addTask(taskOne);
        Task taskTwo = new Task("Task 2", "описание TaskTwo", Status.NEW);
        taskManager.addTask(taskTwo);
        int one = taskOne.getId();
        int two = taskTwo.getId();
        Assertions.assertNotEquals(one, two, "Объекты Task равны");
        Assertions.assertNotNull(taskManager.getTaskId(one), "Такого Task объекта нет в менеджере");
        Assertions.assertNotNull(taskManager.getTaskId(two), "Такого Task объекта нет в менеджере");
    }

    @Test
    void testTaskHistoryPreservation() { // Тест-метод по проверки сохранения разной версии задачи
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task1);
        task1.setStatus(Status.IN_PROGRESS);
        historyManager.add(task1);
        List<Task> histiry = historyManager.getHistory();
        Assertions.assertEquals(2, histiry.size(), "В истории просмотров должно быть 2 задачи");
        Task task = histiry.get(0);
        Task task2 = histiry.get(1);
        Assertions.assertNotEquals(task.getStatus(), task2.getStatus(), "Сохраняются одинаковые версии задач");
    }

    @Test
    void testTaskImmutabilityOnAdd() { //Тест метод по проверки неизменности полей при добавлении в менеджер задач
        Task task = new Task("Task", "описание Task", Status.NEW);
        String name = task.getTask();
        String detalis = task.getDetails();
        Status status = task.getStatus();
        taskManager.addTask(task);
        Task task2 = taskManager.getTaskId(task.getId());
        Assertions.assertEquals(name, task2.getTask(), "Сохраняются разные наименования задач в менеджере");
        Assertions.assertEquals(detalis, task2.getDetails(), "Сохраняются разное описания задач в менеджере");
        Assertions.assertEquals(status, task2.getStatus(), "Сохраняются разный статус задач в менеджере");
    }

    @Test
    void testGetHistoryReturnsCorrectTasks() { //Тест метод по получению списка истории просмотров задач;
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history, "Лист истории пустой");
        Assertions.assertEquals(1, history.size(), "Размер листа истории не соответствует");
    }

    @Test
    void testGetTasksReturnsListOfTasks() { //Тест метод возвращает список Task задач
        List<Task> tasks = taskManager.getListTask();
        Assertions.assertNotNull(tasks, "Лист истории пустой");
        Assertions.assertEquals(1, tasks.size(), "Размер листа истории не соответствует");
    }

    @Test
    void testGetTasksReturnsListOfEpics() { //Тест метод возвращает список Epic задач
        List<Epic> tasks = taskManager.getListEpic();
        Assertions.assertNotNull(tasks, "Лист истории пустой");
        Assertions.assertEquals(1, tasks.size(), "Размер листа истории не соответствует");
    }

    @Test
    void testGetTasksReturnsListOfSubTasks() { //Тест метод возвращает список SubTask задач
        List<SubTask> tasks = taskManager.getListSubTask();
        Assertions.assertNotNull(tasks, "Лист истории пустой");
        Assertions.assertEquals(1, tasks.size(), "Размер листа истории не соответствует");
    }

    @Test
    void testDeleteTaskRemovesSpecifiedTask() { // Tест метод по удалению Task задач
        taskManager.deleteAllTask();
        Task tasks = taskManager.getTaskId(1);
        Assertions.assertNull(null, "Список Task задач не очистился");
    }

    @Test
    void testDeleteEpicTaskRemovesSpecifiedEpic() { //Tест метод по удалению Epic задач
        taskManager.deleteAllEpic();
        Task tasks = taskManager.getEpicId(2);
        Assertions.assertNull(null, "Список Task задач не очистился");
    }

    @Test
    void testDeleteSubtaskTaskRemovesSpecifiedSubtask() { //Tест метод по удалению SubTask задач
        taskManager.deleteAllSubTask();
        Task tasks = taskManager.getSubTaskId(3);
        Assertions.assertNull(null, "Список Task задач не очистился");
    }

    // тест метод по обновлению статуса EPic задачи при изменении статуса у SubTask
    @Test
    void testUpdateTaskUpdatesSpecifiedTask() {
        subTask1.setStatus(Status.IN_PROGRESS);
        epic1.updateStatusEpic();
        Assertions.assertEquals(epic1.getStatus(), subTask1.getStatus(), "Epic задача не обновляется");
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
        Assertions.assertTrue(subTasks.size() > 0, "Список пуст");

    }
}