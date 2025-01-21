package taskTracker;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id = 1;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public ArrayList<Task> getListTask() { // Метод получения Task листа
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getListEpic() { // Метод получения Epic листа
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getListSubTask() { // Метод получения SubTask листа
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllTask() { // Метод удаления всего Task листа
        tasks.clear();
    }

    public void deleteAllEpic() { // Метод удаления всего Epic листа
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubTask() { // Метод удаления всего SubTask листа
        for (Epic epic : epics.values()) {
            epic.deleteSubTasks();
            epic.updateStatusEpic();
        }
        subTasks.clear();
    }

    public Task getTaskId(int id) { // Метод получения Task по id
        return tasks.get(id);
    }

    public Epic getEpicId(int id) { // Метод получения Epic по id
        return epics.get(id);
    }

    public SubTask getSubTaskId(int id) { // Метод получения SubTask по id
        return subTasks.get(id);
    }

    public void addTask(Task task) { // Метод добавления Task
        if (tasks.containsKey(task.getId())) {
            return;
        } else {
            task.setId(id++);
            tasks.put(task.getId(), task);
        }
    }

    public void addEpic(Epic epic) { // Метод добавления Epic
        if (epics.containsKey(epic.getId())) {
            return;
        } else {
            epic.setId(id++);
            epics.put(epic.getId(), epic);
        }
    }

    public void addSubTask(SubTask subTask) { // Метод добавления SubTask + обновление Epic
        if (subTasks.containsKey(subTask.getId())) {
            return;
        } else {
            subTask.setId(id++);
            if (epics.containsKey(subTask.getEpicId())) {
                subTasks.put(subTask.getId(), subTask);
                Epic epic = epics.get(subTask.getEpicId());
                epic.getSubTasks().add(subTask);
                epic.updateStatusEpic();
            }
        }
    }

    public void updateTask(Task task) { // Метод обновления Task
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) { // Метод обновления Epic + обновление данного эпика
        epics.put(epic.getId(), epic);
        epic.updateStatusEpic();
    }

    public void updateSubTask(SubTask subTask) { // Метод обновления subTask + обновление Epic
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.updateStatusEpic();
        }
    }

    public void deletedTaskById(int id) { // Метод удаления Task по id
        tasks.remove(id);
    }

    public void deletedEpicById(int id) { // Метод удаления Epic по id
        Epic epic = epics.get(id);
        ArrayList<SubTask> subTask = epic.getSubTasks();
        for (SubTask subTask1 : subTask) {
            subTasks.remove(subTask1.getId());
        }
        epics.remove(id);
    }

    public void deletedSubTaskById(int id) { // Метод удаления SubTask по id + обновление Epic
        SubTask subtask = subTasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubTasks().remove(subtask);
            epic.updateStatusEpic();
        }
        subTasks.remove(id);
    }

    public ArrayList<SubTask> allSubTaskByEpic(int epicID) { // Метод получения SubTask определенного Epic
        ArrayList<SubTask> subTasksByEpic = new ArrayList<>();
        if (epics.containsKey(epicID)) {
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getEpicId() == epicID) {
                    subTasksByEpic.add(subTask);
                }
            }
        }
        return subTasksByEpic;
    }
}
