package tasktracker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;

class FileBackedTaskManagerTest {

    private File file;
    private FileBackedTaskManager taskManager;
    private Task task1;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;

    @BeforeEach
    void beforeEach() throws IOException {
        file = Files.createTempFile("tasks", ".cvs").toFile();
        taskManager = new FileBackedTaskManager(file);
        task1 = new Task("Задача Task 1", "Описание задачи Task 1", Status.NEW);
        epic1 = new Epic("Epic 1", "Описание 1 Epic задачи");
        epic2 = new Epic("Epic 2", "Описание 2 Epic задачи");
        subTask1 = new SubTask("SubTask 1", "Описание subTask 1", epic1.getId());
        subTask2 = new SubTask("SubTask 2", "Описание subTask 2", epic1.getId());
        subTask3 = new SubTask("SubTask 3", "Описание subTask 3", epic1.getId());
    }

    @Test
    void testSaveToFile() {
        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
            String line = bufferedReader.readLine();
            Assertions.assertNotNull(line, "Задачи не сохраняются в файл");
            Assertions.assertEquals("id,type,name,status,description,epic ", line, "В "
                    + "файл сохраняется информация другого типа");
        } catch (FileNotFoundException e) {
            System.out.println("Файл не существует");
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл");
        }
    }

    @Test
    void testLoadFromFile() throws ManagerSaveException {
        taskManager.addTask(task1);
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);
        Task task = taskManager2.getTaskId(task1.getId());
        Assertions.assertNotNull(task, "Задачи из файла не заполняется в hashMap");
        Assertions.assertEquals(task, task1, "Задачи разные");
    }
}