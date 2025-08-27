package com.balza.todoapp.controller;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.exception.TaskNotFoundException;
import com.balza.todoapp.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@DisplayName("Тесты для TaskController")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    @DisplayName("POST /api/tasks - Должен создать задачу и вернуть 201 Created")
    void createTaskWhenValidRequestThenReturn201() throws Exception {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto("New Task", "Description", null, "TODO");
        TaskResponseDto responseDto = new TaskResponseDto(1L, "New Task", "Description", null, "TODO");

        when(taskService.createTask(any(CreateTaskRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    @DisplayName("POST /api/tasks - Должен вернуть 400 Bad Request при невалидном DTO")
    void createTaskWhenInvalidRequestThenReturn400() throws Exception {
        CreateTaskRequestDto requestDtoWithEmptyTitle = new CreateTaskRequestDto("", "Description", null, "TODO");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoWithEmptyTitle)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Должен вернуть задачу, если она существует")
    void getTaskByIdWhenExistsShouldReturnTask() throws Exception {
        long taskId = 1L;
        TaskResponseDto responseDto = new TaskResponseDto(taskId, "Test Task", null, null, "TODO");
        when(taskService.getById(taskId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Должен вернуть 404, если задача не найдена")
    void getTaskByIdWhenNotFoundShouldReturn404() throws Exception {
        long taskId = 99L;
        when(taskService.getById(taskId)).thenThrow(new TaskNotFoundException("Task not found"));

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tasks - Должен вернуть список всех задач")
    void getAllTasksShouldReturnListOfTasks() throws Exception {
        List<TaskResponseDto> tasks = List.of(
                new TaskResponseDto(1L, "Task 1", null, null, "TODO"),
                new TaskResponseDto(2L, "Task 2", null, null, "DONE")
        );
        when(taskService.findAll()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Должен вернуть 204 No Content при успешном удалении")
    void deleteTaskWhenExistsShouldReturn204() throws Exception {
        long taskId = 1L;
        // Для void методов моки не настраиваются через when/thenReturn

        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Должен вернуть 404 при попытке удалить несуществующую задачу")
    void deleteTaskWhenNotFoundShouldReturn404() throws Exception {
        long taskId = 99L;
        doThrow(new TaskNotFoundException("Task not found")).when(taskService).deleteById(taskId);

        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }
}