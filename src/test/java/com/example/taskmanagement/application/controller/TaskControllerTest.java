package com.example.taskmanagement.application.controller;

import com.example.taskmanagement.application.dto.TaskCreateDTO;
import com.example.taskmanagement.application.dto.TaskDTO;
import com.example.taskmanagement.domain.model.Task;
import com.example.taskmanagement.domain.port.in.TaskUseCase;
import com.example.taskmanagement.util.BearerTokenAuthenticationFilter;
import com.example.taskmanagement.util.MockAuthentication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private TaskUseCase taskUseCase;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).addFilters(new BearerTokenAuthenticationFilter()).build();
        objectMapper = new ObjectMapper();
        mockAuthentication = new MockAuthentication("testUser", "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);
    }

    @Test
    @WithMockUser(username = "user", password = "test", authorities = "ROLE_USER")
    public void createTask_ShouldReturnCreatedTask() throws Exception {
        TaskCreateDTO taskCreateDTO = new TaskCreateDTO("Test Task", "Test Description");
        Task createdTask = new Task("1", "Test Task", "Test Description", false, LocalDateTime.now(), LocalDateTime.now(), "user");

        when(taskUseCase.createTask(any(Task.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer mock-token")
                        .with(authentication(mockAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    public void getTaskById_ShouldReturnTask() throws Exception {
        Task task = new Task("1", "Test Task", "Test Description", false, LocalDateTime.now(), LocalDateTime.now(), "testUser");
        TaskDTO taskDTO = new TaskDTO("1", "Test Task", "Test Description", false, LocalDateTime.now(), LocalDateTime.now());

        when(taskUseCase.getTaskById("1")).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/tasks/1")
                        .with(authentication(mockAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    public void getAllTasks_ShouldReturnListOfTasks() throws Exception {
        Task task1 = new Task("1", "Task 1", "Description 1", false, LocalDateTime.now(), LocalDateTime.now(), "testUser");
        Task task2 = new Task("2", "Task 2", "Description 2", true, LocalDateTime.now(), LocalDateTime.now(), "testUser");
        List<TaskDTO> taskDTOs = Arrays.asList(
                new TaskDTO("1", "Task 1", "Description 1", false, LocalDateTime.now(), LocalDateTime.now()),
                new TaskDTO("2", "Task 2", "Description 2", true, LocalDateTime.now(), LocalDateTime.now())
        );

        when(taskUseCase.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/api/tasks")
                        .with(authentication(mockAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    public void updateTask_ShouldReturnUpdatedTask() throws Exception {
        TaskCreateDTO taskUpdateDTO = new TaskCreateDTO("Updated Task", "Updated Description");
        Task updatedTask = new Task("1", "Updated Task", "Updated Description", true, LocalDateTime.now(), LocalDateTime.now(), "testUser");
        TaskDTO taskDTO = new TaskDTO("1", "Updated Task", "Updated Description", true, LocalDateTime.now(), LocalDateTime.now());

        when(taskUseCase.updateTask(any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/1")
                        .with(authentication(mockAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    public void deleteTask_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/1")
                        .with(authentication(mockAuthentication)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void markTaskAsCompleted_ShouldReturnCompletedTask() throws Exception {
        Task completedTask = new Task("1", "Test Task", "Test Description", true, LocalDateTime.now(), LocalDateTime.now(), "testUser");
        TaskDTO taskDTO = new TaskDTO("1", "Test Task", "Test Description", true, LocalDateTime.now(), LocalDateTime.now());

        when(taskUseCase.markTaskAsCompleted("1")).thenReturn(completedTask);

        mockMvc.perform(patch("/api/tasks/1/complete")
                        .with(authentication(mockAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(true));
    }
}