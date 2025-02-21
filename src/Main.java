import tasktracker.Managers;
import tasktracker.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = new Epic("Epic 1", "Описание 1 Epic задачи");
        Epic epic2 = new Epic("Epic 2", "Описание 2 Epic задачи");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        SubTask subTask1 = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Описание subTask 2", epic1.getId());
        SubTask subTask3 = new SubTask("SubTask 3", "Описание subTask 3", epic1.getId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);

        System.out.println(taskManager.getEpicId(epic1.getId()));
        System.out.println(taskManager.getEpicId(epic2.getId()));
        System.out.println(taskManager.getSubTaskId(subTask1.getId()));
        System.out.println(taskManager.getSubTaskId(subTask2.getId()));
        System.out.println(taskManager.getSubTaskId(subTask3.getId()));
        System.out.println(taskManager.getSubTaskId(subTask1.getId()));
        System.out.println(taskManager.getEpicId(epic2.getId()));
        System.out.println(taskManager.getSubTaskId(subTask3.getId()));
        System.out.println("\n");
        System.out.println(taskManager.getHistory());
        System.out.println("\n");
        taskManager.deletedSubTaskById(subTask1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println("\n");
        taskManager.deletedEpicById(epic1.getId());
        System.out.println(taskManager.getHistory());
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getListTask()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getListEpic()) {
            System.out.println(epic);
            for (Task task : manager.allSubTaskByEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getListSubTask()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
