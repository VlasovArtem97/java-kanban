package taskTracker;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    ArrayList<Task> getListTask();

    ArrayList<Epic> getListEpic();

    ArrayList<SubTask> getListSubTask();

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubTask();

    Task getTaskId(int id);

    Epic getEpicId(int id);

    SubTask getSubTaskId(int id);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void deletedTaskById(int id);

    void deletedEpicById(int id);

    void deletedSubTaskById(int id);

    ArrayList<SubTask> allSubTaskByEpic(int epicID);
}
