package com.example.taskmanagement.application.controller;

import com.example.taskmanagement.application.dto.TaskCreateDTO;
import com.example.taskmanagement.application.dto.TaskDTO;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.port.in.TaskUseCase;
import com.example.taskmanagement.exception.TaskNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Gestión de Tareas", description = "APIs para gestionar tareas")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {

    private final TaskUseCase taskUseCase;

    @PostMapping
    @Operation(summary = "Crear una nueva tarea")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskCreateDTO taskCreateDTO, Authentication authentication) {
        Task task = convertToEntity(taskCreateDTO);
        task.setUserId(authentication.getName());
        Task createdTask = taskUseCase.createTask(task);
        return new ResponseEntity<>(convertToDTO(createdTask), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una tarea por ID")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable String id, Authentication authentication) {
        return taskUseCase.getTaskById(id)
                .filter(task -> task.getUserId().equals(authentication.getName()))
                .map(task -> ResponseEntity.ok(convertToDTO(task)))
                .orElseThrow(() -> new TaskNotFoundException("Tarea no encontrada con id: " + id));
    }

    @GetMapping
    @Operation(summary = "Obtener todas las tareas")
    public ResponseEntity<List<TaskDTO>> getAllTasks(Authentication authentication) {
        List<Task> tasks = taskUseCase.getAllTasks().stream()
                .filter(task -> task.getUserId().equals(authentication.getName()))
                .toList();
        List<TaskDTO> taskDTOs = tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una tarea")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable String id, @Valid @RequestBody TaskCreateDTO taskCreateDTO, Authentication authentication) {
        Task task = convertToEntity(taskCreateDTO);
        task.setId(id);
        task.setUserId(authentication.getName());
        Task updatedTask = taskUseCase.updateTask(task);
        return ResponseEntity.ok(convertToDTO(updatedTask));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una tarea")
    public ResponseEntity<Void> deleteTask(@PathVariable String id, Authentication authentication) {
        taskUseCase.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Marcar una tarea como completada")
    public ResponseEntity<TaskDTO> markTaskAsCompleted(@PathVariable String id, Authentication authentication) {
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
                null,
                null
        );
    }
}

