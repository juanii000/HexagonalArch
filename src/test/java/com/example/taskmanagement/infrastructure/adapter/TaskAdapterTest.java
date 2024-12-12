package com.example.taskmanagement.infrastructure.adapter;

import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.infrastructure.entity.TaskEntity;
import com.example.taskmanagement.infrastructure.repository.TaskInfraRepository;
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

class TaskAdapterTest {

    @Mock
    private TaskInfraRepository taskRepository;

    @InjectMocks
    private TaskAdapter taskAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldReturnSavedTask() {
        Task task = new Task(null, "Test Task", "Test Description", false, null, null,"1");
        TaskEntity savedTaskEntity = new TaskEntity("1", "Test Task", "Test Description", false, LocalDateTime.now(), LocalDateTime.now(),"1");

        when(taskRepository.save(any(TaskEntity.class))).thenReturn(savedTaskEntity);

        Task result = taskAdapter.save(task);

        assertNotNull(result);
        assertEquals(savedTaskEntity.getId(), result.getId());
        assertEquals(savedTaskEntity.getTitle(), result.getTitle());
        assertEquals(savedTaskEntity.getDescription(), result.getDescription());
        assertEquals(savedTaskEntity.isCompleted(), result.isCompleted());
        assertEquals(savedTaskEntity.getCreatedAt(), result.getCreatedAt());
        assertEquals(savedTaskEntity.getUpdatedAt(), result.getUpdatedAt());

        verify(taskRepository, times(1)).save(any(TaskEntity.class));
    }

    @Test
    void findById_ShouldReturnTask_WhenTaskExists() {
        String taskId = "1";
        TaskEntity taskEntity = new TaskEntity(taskId, "Test Task", "Test Description", false, LocalDateTime.now(), LocalDateTime.now(),"1");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        Optional<Task> result = taskAdapter.findById(taskId);

        assertTrue(result.isPresent());
        assertEquals(taskEntity.getId(), result.get().getId());
        assertEquals(taskEntity.getTitle(), result.get().getTitle());
        assertEquals(taskEntity.getDescription(), result.get().getDescription());
        assertEquals(taskEntity.isCompleted(), result.get().isCompleted());
        assertEquals(taskEntity.getCreatedAt(), result.get().getCreatedAt());
        assertEquals(taskEntity.getUpdatedAt(), result.get().getUpdatedAt());

        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenTaskDoesNotExist() {
        String taskId = "1";

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Optional<Task> result = taskAdapter.findById(taskId);

        assertFalse(result.isPresent());

        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void findAll_ShouldReturnListOfTasks() {
        List<TaskEntity> taskEntities = Arrays.asList(
            new TaskEntity("1", "Task 1", "Description 1", false, LocalDateTime.now(), LocalDateTime.now(),"1"),
            new TaskEntity("2", "Task 2", "Description 2", true, LocalDateTime.now(), LocalDateTime.now(),"2")
        );

        when(taskRepository.findAll()).thenReturn(taskEntities);

        List<Task> result = taskAdapter.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(taskEntities.get(0).getId(), result.get(0).getId());
        assertEquals(taskEntities.get(0).getTitle(), result.get(0).getTitle());
        assertEquals(taskEntities.get(1).getId(), result.get(1).getId());
        assertEquals(taskEntities.get(1).getTitle(), result.get(1).getTitle());

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void deleteById_ShouldDeleteTask() {
        String taskId = "1";

        taskAdapter.deleteById(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }
}

