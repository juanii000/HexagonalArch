package com.example.taskmanagement.infrastructure.adapter;

import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.port.out.TaskRepository;
import com.example.taskmanagement.infrastructure.entity.TaskEntity;

import com.example.taskmanagement.infrastructure.repository.TaskInfraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskAdapter implements TaskRepository {

    private final TaskInfraRepository taskRepository;

    @Override
    public Task save(Task task) {
        TaskEntity taskEntity = convertToEntity(task);
        if (taskEntity.getCreatedAt() == null) {
            taskEntity.setCreatedAt(LocalDateTime.now());
        }
        taskEntity.setUpdatedAt(LocalDateTime.now());
        TaskEntity savedTaskEntity = taskRepository.save(taskEntity);
        return convertToDomainModel(savedTaskEntity);
    }

    @Override
    public Optional<Task> findById(String id) {
        return taskRepository.findById(id).map(this::convertToDomainModel);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll().stream()
                .map(this::convertToDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        taskRepository.deleteById(id);
    }

    private TaskEntity convertToEntity(Task task) {
        return new TaskEntity(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getUserId()
        );
    }

    private Task convertToDomainModel(TaskEntity taskEntity) {
        return new Task(
                taskEntity.getId(),
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.isCompleted(),
                taskEntity.getCreatedAt(),
                taskEntity.getUpdatedAt(),
                taskEntity.getUserId()
        );
    }
}

