package tasks;

import tasktracker.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(String task, String details, int epicId, LocalDateTime startTime, Duration duration) {
        super(task, details, Status.NEW, startTime, duration);
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
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
