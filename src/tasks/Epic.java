package tasks;

import tasktracker.TypeTask;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic(String task, String details) {
        super(task, details, Status.NEW);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void deleteSubTasks() {
        subTasks.clear();
    }

    public void updateStatusEpic() {
        if (subTasks.isEmpty()) {
            setStatus(Status.NEW);
        }

        boolean allDone = true;
        boolean inProgress = false;

        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() == Status.NEW) {
                allDone = false;
            } else if (subTask.getStatus() == Status.IN_PROGRESS) {
                allDone = false;
                inProgress = true;
            }
        }

        if (allDone) {
            setStatus(Status.DONE);
        } else if (inProgress) {
            setStatus(Status.IN_PROGRESS);
        } else {
            setStatus(Status.NEW);
        }
    }

    @Override
    public TypeTask getTypeTask() {
        return TypeTask.EPIC;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "subTasks" + subTasks +
                ", task='" + task + '\'' +
                ", details='" + details + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
