package taskTracker;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private final int MAX_SIZE_HISTORY = 10;

    @Override
    public void add(Task task) {
        if (Objects.isNull(task)) {
            return;
        }
        history.add(task.getShapShot());
        if (history.size() > MAX_SIZE_HISTORY) {
            history.remove(0);
        }

    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
