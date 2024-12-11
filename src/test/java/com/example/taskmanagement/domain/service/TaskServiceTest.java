package com.example.taskmanagement.domain.service;

import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.port.out.TaskRepository;
import com.example.taskmanagement.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTask_ShouldReturnCreatedTask() {
        Task task = new Task(null, "Test Task", "Test Description", false, null, null);
        Task createdTask = new Task("1", "Test Task", "Test Description", false, LocalDateTime.now(), LocalDateTime.now());

        when(taskRepository.save(any(Task.class))).thenReturn(createdTask);

        Task result = taskService.createTask(task);

        assertNotNull(result);
        assertEquals(createdTask.getId(), result.getId());
        assertEquals(createdTask.getTitle(), result.getTitle());
        assertEquals(createdTask.getDescription(), result.getDescription());
        assertEquals(createdTask.isCompleted(), result.isCompleted());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenTaskExists() {
        String taskId = "1";
        Task task = new Task(taskId, "Test Task", "Test Description", false, LocalDateTime.now(), LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Optional<Task> result = taskService.getTaskById(taskId);

        assertTrue(result.isPresent());
        assertEquals(task, result.get());

        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getTaskById_ShouldReturnEmpty_WhenTaskDoesNotExist() {
        String taskId = "1";

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.getTaskById(taskId);

        assertFalse(result.isPresent());

        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() {
        List<Task> tasks = Arrays.asList(
                new Task("1", "Task 1", "Description 1", false, LocalDateTime.now(), LocalDateTime.now()),
                new Task("2", "Task 2", "Description 2", true, LocalDateTime.now(), LocalDateTime.now())
        );

        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(tasks, result);

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask_WhenTaskExists() {
        String taskId = "1";
        Task existingTask = new Task(taskId, "Existing Task", "Existing Description", false, LocalDateTime.now(), LocalDateTime.now());
        Task updatedTask = new Task(taskId, "Updated Task", "Updated Description", true, existingTask.getCreatedAt(), LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = taskService.updateTask(updatedTask);

        assertNotNull(result);
        assertEquals(updatedTask.getId(), result.getId());
        assertEquals(updatedTask.getTitle(), result.getTitle());
        assertEquals(updatedTask.getDescription(), result.getDescription());
        assertEquals(updatedTask.isCompleted(), result.isCompleted());
        assertEquals(updatedTask.getCreatedAt(), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
        String taskId = "1";
        Task updatedTask = new Task(taskId, "Updated Task", "Updated Description", true, LocalDateTime.now(), LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(updatedTask));

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask_WhenTaskExists() {
        String taskId = "1";
        Task existingTask = new Task(taskId, "Existing Task", "Existing Description", false, LocalDateTime.now(), LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void deleteTask_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
        String taskId = "1";

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(taskId));

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).deleteById(any(String.class));
    }
    @Test
    void markTaskAsCompleted_ShouldReturnCompletedTask_WhenTaskExists() {
        String taskId = "1";
        Task existingTask = new Task(taskId, "Existing Task", "Existing Description", false, LocalDateTime.now(), LocalDateTime.now());
        Task completedTask = new Task(taskId, "Existing Task", "Existing Description", true, existingTask.getCreatedAt(), LocalDateTime.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(completedTask);

        Task result = taskService.markTaskAsCompleted(taskId);

        assertNotNull(result);
        assertTrue(result.isCompleted());
        assertEquals(completedTask.getId(), result.getId());
        assertEquals(completedTask.getTitle(), result.getTitle());
        assertEquals(completedTask.getDescription(), result.getDescription());
        assertEquals(completedTask.getCreatedAt(), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void markTaskAsCompleted_ShouldThrowTaskNotFoundException_WhenTaskDoesNotExist() {
        String taskId = "1";

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.markTaskAsCompleted(taskId));

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }
}