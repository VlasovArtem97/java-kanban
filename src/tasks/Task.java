package tasks;

public class Task {
    protected final String task;
    protected final String details;
    protected int id;
    protected Status status;

    public Task(String task, String details, Status status) {
        this.task = task;
        this.details = details;
        this.status = status;
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
                '}';
    }
}
