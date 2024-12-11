package com.example.taskmanagement.domain.port.out;

import com.example.taskmanagement.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(String id);
    List<Task> findAll();
    void deleteById(String id);
}

