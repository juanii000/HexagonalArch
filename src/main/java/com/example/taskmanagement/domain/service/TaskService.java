package com.example.taskmanagement.domain.service;

import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.port.in.TaskUseCase;
import com.example.taskmanagement.domain.port.out.TaskRepository;
import com.example.taskmanagement.exception.TaskNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService implements TaskUseCase {

    private final TaskRepository taskRepository;

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task updateTask(Task task) {
        if (!taskRepository.findById(task.getId()).isPresent()) {
            throw new TaskNotFoundException("Task not found with id: " + task.getId());
        }
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(String id) {
        if (!taskRepository.findById(id).isPresent()) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public Task markTaskAsCompleted(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        task.setCompleted(true);
        return taskRepository.save(task);
    }
}

