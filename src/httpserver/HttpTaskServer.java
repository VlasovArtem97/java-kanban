package httpserver;

import com.sun.net.httpserver.HttpServer;
import tasktracker.Managers;
import tasktracker.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager managers;

    public HttpTaskServer(TaskManager managers) throws IOException {
        this.managers = managers;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.server.createContext("/tasks", new TaskHandler(this.managers));
        this.server.createContext("/epics", new EpicHandler(this.managers));
        this.server.createContext("/subtasks", new SubTasksHandler(this.managers));
        this.server.createContext("/history", new HistoryHandler(this.managers));
        this.server.createContext("/prioritized", new PrioritizedHandler(this.managers));
        server.setExecutor(null);
    }

    public static void main(String[] arg) throws IOException {
        final String LOCATION = "src/resources/tasks.csv";

        File file = Paths.get(LOCATION).toFile();
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault(file));
        httpTaskServer.start();
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту 8080");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен на порту 8080");
    }
}
