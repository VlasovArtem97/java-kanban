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
import java.time.Duration;
import java.time.LocalDateTime;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file = Files.createTempFile("tasks", ".csv").toFile();
    private static FileBackedTaskManager taskManager;
    private static Task task1;
    private static Epic epic1;
    private static Epic epic2;
    private static SubTask subTask1;
    private static SubTask subTask2;
    private static SubTask subTask3;

    FileBackedTaskManagerTest() throws IOException {
    }

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(file);
    }

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
        subTask3 = new SubTask("SubTask 3", "Описание subTask 3", epic1.getId(),
                LocalDateTime.of(2024, 3, 29, 15, 0), Duration.ofHours(1));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
    }

    @Test
    void testSaveToFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
            String line = bufferedReader.readLine();
            Assertions.assertNotNull(line, "Задачи не сохраняются в файл");
            Assertions.assertEquals("id,type,name,status,description,startTime,duration,epic ", line, "В "
                    + "файл сохраняется информация другого типа");
        } catch (FileNotFoundException e) {
            System.out.println("Файл не существует");
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл");
        }
    }

    @Test
    void testLoadFromFile() {
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);
        Task task = taskManager2.getTaskId(task1.getId());
        Assertions.assertNotNull(task, "Задачи из файла не заполняется в hashMap");
        Assertions.assertEquals(task, task1, "Задачи разные");
    }
}