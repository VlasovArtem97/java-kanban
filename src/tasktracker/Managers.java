package tasktracker;

import java.io.File;
import java.io.IOException;

public final class Managers {

    private Managers() {
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(File file) throws IOException, ManagerSaveException {
        return FileBackedTaskManager.loadFromFile(file);
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
