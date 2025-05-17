package tasktracker;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 1;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final TreeSet<Task> priorityTask;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        priorityTask = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(priorityTask);
    }

    public boolean isIntersection(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }
        return start2.isBefore(end1) && start1.isBefore(end2) || start1.equals(start2) && end1.equals(end2);
    }


    public boolean isTaskIntersecting(Task newTask) {
        return priorityTask.stream()
                .anyMatch(exTask -> isIntersection(exTask, newTask));
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
        tasks.values()
                .forEach(priorityTask::remove);
        tasks.keySet()
                .forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() { // Метод удаления всего Epic листа
        epics.keySet()
                .forEach(historyManager::remove);
        subTasks.keySet()
                .forEach(historyManager::remove);
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTask() { // Метод удаления всего SubTask листа
        epics.values()
                .forEach(epic -> {
                    epic.deleteSubTasks();
                    epic.updateStatusEpic();
                    epic.updateEpicEndTime();
                });
        subTasks.values()
                .forEach(priorityTask::remove);
        subTasks.keySet()
                .forEach(historyManager::remove);
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
            if (!isTaskIntersecting(task)) {
                task.setId(id++);
                tasks.put(task.getId(), task);
                if (task.getStartTime() != null) {
                    priorityTask.add(task);
                }
            } else {
                throw new IllegalArgumentException("Добавленная задача пересекается с другими задачами по времени " +
                        "выполнения = " + task);
            }
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
            if (!isTaskIntersecting(subTask)) {
                subTask.setId(id++);
                if (epics.containsKey(subTask.getEpicId())) {
                    subTasks.put(subTask.getId(), subTask);
                    Epic epic = epics.get(subTask.getEpicId());
                    epic.getSubTasks().add(subTask);
                    epic.updateStatusEpic();
                    epic.updateEpicEndTime();
                    if (subTask.getStartTime() != null) {
                        priorityTask.add(subTask);
                    }
                }
            } else {
                throw new IllegalArgumentException("Добавленная задача пересекается с другими задачами по времени " +
                        "выполнения = " + subTask);
            }
        }
    }

    @Override
    public void updateTask(Task task) { // Метод обновления Task
        Task task1 = tasks.get(task.getId());
        priorityTask.remove(task1);
        if (isTaskIntersecting(task)) {
            priorityTask.add(task1);
            throw new IllegalArgumentException("В обновленной Task задаче установленное время " +
                    "пересекается с другой задачей по времени выполнения = " + task);
        }
        priorityTask.add(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) { // Метод обновления Epic + обновление данного эпика
        epics.put(epic.getId(), epic);
        epic.updateStatusEpic();
    }

    @Override
    public void updateSubTask(SubTask subTask) { // Метод обновления subTask + обновление Epic
        Optional<SubTask> subTaskOne = subTasks.values().stream()
                .filter(subTask2 -> subTask2.getId() == subTask.getId())
                .findFirst();
        SubTask subTask1 = subTaskOne.orElseThrow(() -> new IllegalArgumentException("Проверь параметры Subtask задачи"));
        priorityTask.remove(subTask1);
        if (isTaskIntersecting(subTask)) {
            priorityTask.add(subTask1);
            throw new IllegalArgumentException("В обновленной subTask задаче установленное время " +
                    "пересекается с другой задачей по времени выполнения = " + subTask);
        } else {
            priorityTask.add(subTask);
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.getSubTasks().remove(subTask1);
                epic.getSubTasks().add(subTask);
                epic.updateStatusEpic();
                epic.updateEpicEndTime();
            }
        }
    }

    @Override
    public void deletedTaskById(int id) { // Метод удаления Task по id
        Task task = tasks.get(id);
        priorityTask.remove(task);
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
            priorityTask.remove(subTask1);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deletedSubTaskById(int id) { // Метод удаления SubTask по id + обновление Epic
        SubTask subtask = subTasks.get(id);
        priorityTask.remove(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubTasks().remove(subtask);
            epic.updateStatusEpic();
            epic.updateEpicEndTime();
        }
        subTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<SubTask> allSubTaskByEpic(int epicID) { // Метод получения SubTask определенного Epic
        if (!epics.containsKey(epicID)) {
            return Collections.emptyList();
        } else {
            return subTasks.values().stream()
                    .filter(subTask -> subTask.getEpicId() == epicID)
                    .collect(Collectors.toList());
        }
    }
}
