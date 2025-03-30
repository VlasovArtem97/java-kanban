import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import tasktracker.Managers;
import tasktracker.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    private static final String LOCATION = "src/resources/tasks.csv";

    public static void main(String[] args) {

        try {
//            File file = Files.createFile(Paths.get(LOCATION)).toFile();
            File file = Paths.get(LOCATION).toFile();
            TaskManager taskManager = Managers.getDefault(file);

            Task task1 = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW,
                    LocalDateTime.of(2024, 3, 29, 12, 0), Duration.ofHours(1));
            Epic epic1 = new Epic("Epic 1", "Описание 1 Epic задачи");
            Epic epic2 = new Epic("Epic 2", "Описание 2 Epic задачи");

            taskManager.addTask(task1);
            taskManager.addEpic(epic1);
            taskManager.addEpic(epic2);

            SubTask subTask1 = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId(),
                    LocalDateTime.of(2024, 3, 29, 13, 0), Duration.ofHours(1));
            SubTask subTask2 = new SubTask("SubTask 2", "Описание subTask 2", epic1.getId(),
                    LocalDateTime.of(2024, 3, 29, 14, 0), Duration.ofHours(1));
            SubTask subTask3 = new SubTask("SubTask 3", "Описание subTask 3", epic1.getId(),
                    LocalDateTime.of(2024, 3, 29, 15, 0), Duration.ofHours(1));

            taskManager.addSubTask(subTask1);
            taskManager.addSubTask(subTask2);
            taskManager.addSubTask(subTask3);

            System.out.println(taskManager.getPrioritizedTasks());

            printAllTasks(taskManager);

        } catch (IOException e) {
            System.out.println("Файл уже создан");
        } catch (ManagerSaveException e) {
            System.out.println("Требуется перезагрузка");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
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
