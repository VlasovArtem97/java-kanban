import taskTracker.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("tasks.Task 1", "Описание 1 tasks.Task задачи", Status.NEW);
        Task task2 = new Task("tasks.Task 2", "Описание 2 задачи", Status.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("tasks.Epic 1", "Описание 1 tasks.Epic задачи");
        Epic epic2 = new Epic("tasks.Epic 2", "Описание 2 tasks.Epic задачи");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        SubTask subTask1 = new SubTask("tasks.SubTask 1", "Описание subTask 1", epic1.getId());
        SubTask subTask2 = new SubTask("tasks.SubTask 2", "Описание subTask 2", epic1.getId());
        SubTask subTask3 = new SubTask("tasks.SubTask 3", "Описание subTask 3", epic2.getId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);

        System.out.println(taskManager.getListTask());
        System.out.println(taskManager.getListEpic());
        System.out.println(taskManager.getListSubTask());

        System.out.println("\n Изменяем статус 1-го subTask и 1-го task, выводим subTask лист , обновляем 1-ю "
                + "подзадачу и выводим список Epics и task \n");
        subTask1.setStatus(Status.IN_PROGRESS);
        task1.setStatus(Status.DONE);
        System.out.println(taskManager.getListSubTask());
        taskManager.updateSubTask(subTask1);
        System.out.println(taskManager.getListEpic());
        System.out.println(taskManager.getListTask());

        System.out.println("\n удаляем 1-й task и выводим список tasks \n");
        taskManager.deletedTaskById(1);
        System.out.println(taskManager.getListTask());

        System.out.println("\n удаляем 2-й epic и выводим список epics \n");
        taskManager.deletedEpicById(epic2.getId());
        System.out.println(taskManager.getListEpic());

        System.out.println("\n удаляем 1-ю subTask и выводим список epics subTasks \n");
        taskManager.deletedSubTaskById(subTask1.getId());
        System.out.println(taskManager.getListEpic());
        System.out.println(taskManager.getListSubTask());
    }
}
