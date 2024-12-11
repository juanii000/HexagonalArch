package com.example.taskmanagement.application.controller;

import com.example.taskmanagement.application.dto.TaskCreateDTO;
import com.example.taskmanagement.application.dto.TaskDTO;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.port.in.TaskUseCase;
import com.example.taskmanagement.exception.TaskNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {

    private final TaskUseCase taskUseCase;

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskCreateDTO taskCreateDTO) {
        Task task = convertToEntity(taskCreateDTO);
        Task createdTask = taskUseCase.createTask(task);
        return new ResponseEntity<>(convertToDTO(createdTask), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable String id) {
        return taskUseCase.getTaskById(id)
                .map(task -> ResponseEntity.ok(convertToDTO(task)))
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @GetMapping
    @Operation(summary = "Get all tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<Task> tasks = taskUseCase.getAllTasks();
        List<TaskDTO> taskDTOs = tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable String id, @Valid @RequestBody TaskCreateDTO taskCreateDTO) {
        Task task = convertToEntity(taskCreateDTO);
        task.setId(id);
        Task updatedTask = taskUseCase.updateTask(task);
        return ResponseEntity.ok(convertToDTO(updatedTask));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskUseCase.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark a task as completed")
    public ResponseEntity<TaskDTO> markTaskAsCompleted(@PathVariable String id) {
        Task completedTask = taskUseCase.markTaskAsCompleted(id);
        return ResponseEntity.ok(convertToDTO(completedTask));
    }

    private TaskDTO convertToDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private Task convertToEntity(TaskCreateDTO taskCreateDTO) {
        return new Task(
                null,
                taskCreateDTO.getTitle(),
                taskCreateDTO.getDescription(),
                false,
                null,
                null
        );
    }
}

