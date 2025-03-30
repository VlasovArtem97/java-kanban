package tasks;

import tasktracker.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<SubTask> subTasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String task, String details) {
        super(task, details, Status.NEW, null, Duration.ZERO);
        this.endTime = null;
    }

    public void updateEpicEndTime() {
        if (subTasks.isEmpty()) {
            this.startTime = null;
            this.endTime = null;
            this.duration = Duration.ZERO;
        } else {
            this.startTime = getStartTime();
            this.endTime = getEndTime();
            this.duration = getDuration();
        }
    }

    @Override
    public Duration getDuration() {
        if (this.startTime != null && this.endTime != null) {
            return Duration.between(this.startTime, this.endTime);
        }
        return Duration.ZERO;
    }

    @Override
    public LocalDateTime getStartTime() {
        return subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public void addSubtaskToEpic(SubTask subTask) {
        this.subTasks.add(subTask);
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
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
