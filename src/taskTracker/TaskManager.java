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
        ArrayList<Task> listTask = new ArrayList<>();
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                listTask.add(task);
            }
            return listTask;
        } else {
            System.out.println("Список Task-задач пуст");
            return null;
        }
    }

    public ArrayList<Epic> getListEpic() { // Метод получения Epic листа
        ArrayList<Epic> listEpic = new ArrayList<>();
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                listEpic.add(epic);
            }
            return listEpic;
        } else {
            System.out.println("Список Epic-задач пуст");
            return null;
        }
    }

    public ArrayList<SubTask> getListSubTask() { // Метод получения SubTask листа
        ArrayList<SubTask> listSubTask = new ArrayList<>();
        if (!subTasks.isEmpty()) {
            for (SubTask subTask : subTasks.values()) {
                listSubTask.add(subTask);
            }
            return listSubTask;
        } else {
            System.out.println("Список SubTask-задач пуст");
            return null;
        }
    }

    public void deleteAllTask() { // Метод удаления всего Task листа
        tasks.clear();
    }

    public void deleteAllEpic() { // Метод удаления всего Epic листа
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubTask() { // Метод удаления всего SubTask листа
        subTasks.clear();
    }

    public Task getTaskId(int id) { // Метод получения Task по id
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            System.out.println("Такого идентификатора Task-задачи не существует");
            return null;
        }
    }

    public Epic getEpicId(int id) { // Метод получения Epic по id
        if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            System.out.println("Такого идентификатора Epic-задачи не существует");
            return null;
        }
    }

    public SubTask getSubTaskId(int id) { // Метод получения SubTask по id
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        } else {
            System.out.println("Такого идентификатора SubTask-задачи не существует");
            return null;
        }
    }

    public void addTask(Task task) { // Метод добавления Task
        if (tasks.containsKey(task.getId())) {
            System.out.println("Данная Task-задача уже существует");
            return;
        } else {
            task.setId(id++);
            tasks.put(task.getId(), task);
        }
    }

    public void addEpic(Epic epic) { // Метод добавления Epic
        if (epics.containsKey(epic.getId())) {
            System.out.println("Данная Epic-задача уже существует");
            return;
        } else {
            epic.setId(id++);
            epics.put(epic.getId(), epic);
        }
    }

    public void addSubTask(SubTask subTask) { // Метод добавления SubTask + обновление Epic
        if (subTasks.containsKey(subTask.getId())) {
            System.out.println("Данная SubTask-задача уже существует");
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
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Такого идентификатора не существует");
        }
    }

    public void deletedEpicById(int id) { // Метод удаления Epic по id
        if (epics.containsKey(id)) {
            epics.remove(id);
        } else {
            System.out.println("Такого идентификатора не существует");
        }
    }

    public void deletedSubTaskById(int id) { // Метод удаления SubTask по id + обновление Epic
        if (subTasks.containsKey(id)) {
            SubTask subtask = subTasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubTasks().remove(subtask);
                epic.updateStatusEpic();
            }
            subTasks.remove(id);
        } else {
            System.out.println("Такого идентификатора не существует");
        }
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
