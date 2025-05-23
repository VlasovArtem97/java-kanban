package tasks;

import tasktracker.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected final String task;
    protected final String details;
    protected int id;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;


    public Task(String task, String details, Status status, LocalDateTime startTime, Duration duration) {
        this.task = task;
        this.details = details;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Task copyTask) {
        this.id = copyTask.id;
        this.task = copyTask.task;
        this.details = copyTask.details;
        this.status = copyTask.status;
        this.startTime = copyTask.startTime;
        this.duration = copyTask.duration;
    }


    public Duration getDuration() {
        return this.duration != null ? duration : Duration.ZERO;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime != null && duration != null ? startTime.plus(duration) : null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public String getDetails() {
        return details;
    }

    public TypeTask getTypeTask() {
        return TypeTask.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task1 = (Task) o;
        return id == task1.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "task='" + task + '\'' +
                ", details='" + details + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
