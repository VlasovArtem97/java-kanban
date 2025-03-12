package tasktracker;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,epic \n");
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

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        final FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String a = bufferedReader.readLine();
                Task task = fromString(a);
                if (task != null) {
                    if (task instanceof Epic) {
                        fileBackedTaskManager.addEpic((Epic) task);
                    } else if (task instanceof SubTask) {
                        fileBackedTaskManager.addSubTask((SubTask) task);
                    } else {
                        fileBackedTaskManager.addTask(task);
                    }
                }
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Работа с файлом в методе loadFromFile() невозможна");
        }
    }

    private String toString(Task task) {
        if (task instanceof Epic epic) {
            return String.format("%s,%s,%s,%s,%s,%n", epic.getId(), epic.getTypeTask(), epic.getTask(),
                    epic.getStatus(), epic.getDetails());
        } else if (task instanceof SubTask subTask) {
            return String.format("%s,%s,%s,%s,%s,%s%n", subTask.getId(), subTask.getTypeTask(), subTask.getTask(),
                    subTask.getStatus(), subTask.getDetails(), subTask.getEpicId());
        } else {
            return String.format("%s,%s,%s,%s,%s,%n", task.getId(), task.getTypeTask(), task.getTask(),
                    task.getStatus(), task.getDetails());
        }
    }

    private static Task fromString(String value) {
        String[] split = value.split(",");
        Task task = null;
        if (split[1].equals("TASK")) {
            if (split[3].equals("NEW")) {
                task = new Task(split[2], split[4], Status.NEW);
            } else if (split[3].equals("IN_PROGRESS")) {
                task = new Task(split[2], split[4], Status.IN_PROGRESS);
            } else {
                task = new Task(split[2], split[4], Status.DONE);
            }
        } else if (split[1].equals("EPIC")) {
            if (split[3].equals("NEW")) {
                task = new Epic(split[2], split[4]);
                task.setStatus(Status.NEW);
            } else if (split[3].equals("IN_PROGRESS")) {
                task = new Epic(split[2], split[4]);
                task.setStatus(Status.IN_PROGRESS);
            } else {
                task = new Epic(split[2], split[4]);
                task.setStatus(Status.DONE);
            }
        } else if (split[1].equals("SUBTASK")) {
            if (split[3].equals("NEW")) {
                String a = split[5];
                task = new SubTask(split[2], split[4], Integer.parseInt(a));
                task.setStatus(Status.NEW);
            } else if (split[3].equals("IN_PROGRESS")) {
                String a = split[5];
                task = new SubTask(split[2], split[4], Integer.parseInt(a));
                task.setStatus(Status.IN_PROGRESS);
            } else {
                String a = split[5];
                task = new SubTask(split[2], split[4], Integer.parseInt(a));
                task.setStatus(Status.DONE);
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


