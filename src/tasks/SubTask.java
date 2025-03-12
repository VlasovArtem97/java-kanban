package tasks;

import tasktracker.TypeTask;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(String task, String details, int epicId) {
        super(task, details, Status.NEW);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TypeTask getTypeTask() {
        return TypeTask.SUBTASK;
    }

    @Override
    public String toString() {
        return "tasks.SubTask{" +
                "epicId=" + epicId +
                ", task='" + task + '\'' +
                ", details='" + details + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
