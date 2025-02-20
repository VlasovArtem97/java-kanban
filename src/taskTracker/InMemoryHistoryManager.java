package taskTracker;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (Objects.isNull(task)) {
            return;
        }
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        removeNode(node);
        nodeMap.remove(id);
    }

    public void linkLast(Task task) {
        Node oldNode = tail;
        Node newNode = new Node(oldNode, task, null);
        tail = newNode;
        if (oldNode == null) {
            head = newNode;
        } else {
            oldNode.setNext(newNode);
        }
        nodeMap.put(task.getId(), newNode);
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        while (Objects.nonNull(node)) {
            tasks.add(node.getTask());
            node = node.getNext();
        }
        return tasks;
    }

    public void removeNode(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();
        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
        }
        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
