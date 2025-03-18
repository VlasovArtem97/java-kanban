package tasktracker;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    List<Task> getListTask();

    List<Epic> getListEpic();

    List<SubTask> getListSubTask();

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubTask();

    Task getTaskId(int id);

    Epic getEpicId(int id);

    SubTask getSubTaskId(int id);

    void addTask(Task task) throws IOException, ManagerSaveException;

    void addEpic(Epic epic) throws IOException, ManagerSaveException;

    void addSubTask(SubTask subTask) throws IOException, ManagerSaveException;

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void deletedTaskById(int id);

    void deletedEpicById(int id);

    void deletedSubTaskById(int id);

    List<SubTask> allSubTaskByEpic(int epicID);
}
