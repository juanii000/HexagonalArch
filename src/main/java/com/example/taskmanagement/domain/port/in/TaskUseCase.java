package com.example.taskmanagement.domain.port.in;

import com.example.taskmanagement.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskUseCase {
    Task createTask(Task task);
    Optional<Task> getTaskById(String id);
    List<Task> getAllTasks();
    Task updateTask(Task task);
    void deleteTask(String id);
    Task markTaskAsCompleted(String id);
}

