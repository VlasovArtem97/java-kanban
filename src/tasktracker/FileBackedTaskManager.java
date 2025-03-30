package tasktracker;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;
    private int maxId = 0;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yy.MM.dd|HH:mm:ss");

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,startTime,duration,epic \n");
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                Task task = entry.getValue();
                bufferedWriter.write(toString(task));
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                Epic task = entry.getValue();
                bufferedWriter.write(toString(task));
            }
            for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
                SubTask task = entry.getValue();
                bufferedWriter.write(toString(task));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Работа с файлом в методе save() невозможна");
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String a = bufferedReader.readLine();
                Task task = fileBackedTaskManager.fromString(a);
                if (task != null) {
                    if (task.getId() > fileBackedTaskManager.maxId) {
                        fileBackedTaskManager.maxId = task.getId();
                    }
                }
            }
            fileBackedTaskManager.id = fileBackedTaskManager.maxId + 1;
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Работа с файлом в методе loadFromFile() невозможна");
        }
    }

    private String toString(Task task) {
        if (task.getTypeTask().equals(TypeTask.EPIC)) {
            Epic epic = (Epic) task;
            return String.format("%s,%s,%s,%s,%s,%s,%s,%n", epic.getId(), epic.getTypeTask(), epic.getTask(),
                    epic.getStatus(), epic.getDetails(),
                    epic.getStartTime() != null ? epic.getStartTime().format(DATE_TIME_FORMATTER) : null,
                    epic.getDuration().toMinutes());
        } else if (task.getTypeTask().equals(TypeTask.SUBTASK)) {
            SubTask subTask = (SubTask) task;
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s%n", subTask.getId(), subTask.getTypeTask(), subTask.getTask(),
                    subTask.getStatus(), subTask.getDetails(),
                    subTask.getStartTime() != null ? subTask.getStartTime().format(DATE_TIME_FORMATTER) : null,
                    subTask.getDuration().toMinutes(), subTask.getEpicId());
        } else {
            return String.format("%s,%s,%s,%s,%s,%s,%s%n", task.getId(), task.getTypeTask(), task.getTask(),
                    task.getStatus(), task.getDetails(),
                    task.getStartTime() != null ? task.getStartTime().format(DATE_TIME_FORMATTER) : null,
                    task.getDuration().toMinutes());
        }
    }

    private Task fromString(String value) {
        String[] split = value.split(",");
        Task task = null;
        switch (split[1]) {
            case "TASK" -> {
                if (split[3].equals("NEW")) {
                    task = new Task(split[2], split[4], Status.NEW, LocalDateTime.parse(split[5], DATE_TIME_FORMATTER),
                            Duration.ofMinutes(Long.parseLong(split[6])));
                    task.setId(Integer.parseInt(split[0]));
                    addTask(task);
                } else if (split[3].equals("IN_PROGRESS")) {
                    task = new Task(split[2], split[4], Status.IN_PROGRESS, LocalDateTime.parse(split[5], DATE_TIME_FORMATTER),
                            Duration.ofMinutes(Long.parseLong(split[6])));
                    task.setId(Integer.parseInt(split[0]));
                    addTask(task);
                } else {
                    task = new Task(split[2], split[4], Status.DONE, LocalDateTime.parse(split[5], DATE_TIME_FORMATTER),
                            Duration.ofMinutes(Long.parseLong(split[6])));
                    task.setId(Integer.parseInt(split[0]));
                    addTask(task);
                }
            }
            case "EPIC" -> {
                if (split[3].equals("NEW")) {
                    task = new Epic(split[2], split[4]);
                    task.setStatus(Status.NEW);
                    task.setId(Integer.parseInt(split[0]));
                    addEpic((Epic) task);
                    ((Epic) task).updateEpicEndTime();
                } else if (split[3].equals("IN_PROGRESS")) {
                    task = new Epic(split[2], split[4]);
                    task.setStatus(Status.IN_PROGRESS);
                    task.setId(Integer.parseInt(split[0]));
                    epics.put(task.getId(), (Epic) task);
                    addEpic((Epic) task);
                    ((Epic) task).updateEpicEndTime();
                } else {
                    task = new Epic(split[2], split[4]);
                    task.setStatus(Status.DONE);
                    task.setId(Integer.parseInt(split[0]));
                    addEpic((Epic) task);
                    ((Epic) task).updateEpicEndTime();
                }
            }
            case "SUBTASK" -> {
                if (split[3].equals("NEW")) {
                    String a = split[7];
                    task = new SubTask(split[2], split[4], Integer.parseInt(a), LocalDateTime.parse(split[5], DATE_TIME_FORMATTER),
                            Duration.ofMinutes(Long.parseLong(split[6])));
                    task.setStatus(Status.NEW);
                    task.setId(Integer.parseInt(split[0]));
                    Epic epic = epics.get(Integer.parseInt(a));
                    addSubTask((SubTask) task);
                    epic.updateEpicEndTime();
                } else if (split[3].equals("IN_PROGRESS")) {
                    String a = split[7];
                    task = new SubTask(split[2], split[4], Integer.parseInt(a), LocalDateTime.parse(split[5], DATE_TIME_FORMATTER),
                            Duration.ofMinutes(Long.parseLong(split[6])));
                    task.setStatus(Status.IN_PROGRESS);
                    task.setId(Integer.parseInt(split[0]));
                    Epic epic = epics.get(Integer.parseInt(a));
                    addSubTask((SubTask) task);
                    epic.updateEpicEndTime();
                } else {
                    String a = split[7];
                    task = new SubTask(split[2], split[4], Integer.parseInt(a), LocalDateTime.parse(split[5], DATE_TIME_FORMATTER),
                            Duration.ofMinutes(Long.parseLong(split[6])));
                    task.setStatus(Status.DONE);
                    task.setId(Integer.parseInt(split[0]));
                    Epic epic = epics.get(Integer.parseInt(a));
                    addSubTask((SubTask) task);
                    epic.updateEpicEndTime();
                }
            }
        }
        return task;
    }

    @Override
    public void addEpic(Epic epic) {
        try {
            super.addEpic(epic);
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Не удается добавить Epic задачу в hashMap: " + e.getMessage());
        }

    }

    @Override
    public void addTask(Task task) {
        try {
            super.addTask(task);
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Не удается добавить Task задачу в hashMap: " + e.getMessage());
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        try {
            super.addSubTask(subTask);
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Не удается добавить SubTask задачу в hashMap: " + e.getMessage());
        }
    }


    @Override
    public void deleteAllTask() {
        try {
            super.deleteAllTask();
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Невозможно удалить все Task задачи: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllEpic() {
        try {
            super.deleteAllEpic();
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Невозможно удалить все Epic задачи: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllSubTask() {
        try {
            super.deleteAllSubTask();
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Невозможно удалить все SubTask задачи: " + e.getMessage());
        }
    }

    @Override
    public void updateTask(Task task) {
        try {
            super.updateTask(task);
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Невозможно обновить Task задачу: " + e.getMessage());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        try {
            super.updateEpic(epic);
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Невозможно обновить Epic задачу: " + e.getMessage());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        try {
            super.updateSubTask(subTask);
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Невозможно обновить SubTask задачу: " + e.getMessage());
        }
    }

    @Override
    public void deletedTaskById(int id) {
        try {
            super.deletedTaskById(id);
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Невозможно удалить Task задачу по id: " + e.getMessage());
        }
    }

    @Override
    public void deletedEpicById(int id) {
        try {
            super.deletedEpicById(id);
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Невозможно удалить Epic задачу по id: " + e.getMessage());
        }
    }

    @Override
    public void deletedSubTaskById(int id) {
        try {
            super.deletedSubTaskById(id);
            save();
        } catch (ManagerSaveException e) {
            System.out.println("Невозможно удалить SubTask задачу по id: " + e.getMessage());
        }
    }
}


