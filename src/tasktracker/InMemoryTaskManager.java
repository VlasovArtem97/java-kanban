package tasktracker;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id = 1;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getListTask() { // Метод получения Task листа
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getListEpic() { // Метод получения Epic листа
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getListSubTask() { // Метод получения SubTask листа
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTask() { // Метод удаления всего Task листа
        for (Integer task : tasks.keySet()) {
            historyManager.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() { // Метод удаления всего Epic листа
        for (Integer task : epics.keySet()) {
            historyManager.remove(task);
        }
        for (Integer task : subTasks.keySet()) {
            historyManager.remove(task);
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTask() { // Метод удаления всего SubTask листа
        for (Epic epic : epics.values()) {
            epic.deleteSubTasks();
            epic.updateStatusEpic();
        }
        for (Integer task : subTasks.keySet()) {
            historyManager.remove(task);
        }
        subTasks.clear();
    }

    @Override
    public Task getTaskId(int id) { // Метод получения Task по id
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicId(int id) { // Метод получения Epic по id
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskId(int id) { // Метод получения SubTask по id
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void addTask(Task task) { // Метод добавления Task
        if (tasks.containsKey(task.getId())) {
            return;
        } else {
            task.setId(id++);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void addEpic(Epic epic) { // Метод добавления Epic
        if (epics.containsKey(epic.getId())) {
            return;
        } else {
            epic.setId(id++);
            epics.put(epic.getId(), epic);
        }
    }

    @Override
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

    @Override
    public void updateTask(Task task) { // Метод обновления Task
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) { // Метод обновления Epic + обновление данного эпика
        epics.put(epic.getId(), epic);
        epic.updateStatusEpic();
    }

    @Override
    public void updateSubTask(SubTask subTask) { // Метод обновления subTask + обновление Epic
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.updateStatusEpic();
        }
    }

    @Override
    public void deletedTaskById(int id) { // Метод удаления Task по id
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deletedEpicById(int id) { // Метод удаления Epic по id
        Epic epic = epics.get(id);
        ArrayList<SubTask> subTask = epic.getSubTasks();
        for (SubTask subTask1 : subTask) {
            subTasks.remove(subTask1.getId());
            historyManager.remove(subTask1.getId());
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deletedSubTaskById(int id) { // Метод удаления SubTask по id + обновление Epic
        SubTask subtask = subTasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubTasks().remove(subtask);
            epic.updateStatusEpic();
        }
        subTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<SubTask> allSubTaskByEpic(int epicID) { // Метод получения SubTask определенного Epic
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
