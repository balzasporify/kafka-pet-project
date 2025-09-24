package com.balza.todoapp.controller;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.exception.TaskNotFoundException;
import com.balza.todoapp.model.Status;
import com.balza.todoapp.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    private final OffsetDateTime testInstant = OffsetDateTime.parse("2025-09-10T10:00:00Z");

    /**
     * Тест для POST /api/v1/tasks.
     * Проверяет успешное создание задачи и возврат статуса 201 Created.
     */
    @Test
    @DisplayName("POST /api/v1/tasks - Должен создать задачу и вернуть 201 Created")
    void createTaskWhenValidRequestThenReturn201() throws Exception {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto("New Task", "Description", testInstant, Status.TODO);
        TaskResponseDto responseDto = new TaskResponseDto(1L, "New Task", "Description", testInstant, Status.TODO);

        when(taskService.createTask(any(CreateTaskRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    /**
     * Тест для POST /api/v1/tasks с невалидными данными.
     * Проверяет, что при невалидном DTO возвращается статус 400 Bad Request.
     */
    @Test
    @DisplayName("POST /api/v1/tasks - Должен вернуть 400 Bad Request при невалидном DTO")
    void createTaskWhenInvalidRequestThenReturn400() throws Exception {
        CreateTaskRequestDto requestDtoWithEmptyTitle = new CreateTaskRequestDto("", "Description", null, Status.TODO);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoWithEmptyTitle)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Тест для GET /api/v1/tasks/{id}.
     * Проверяет успешное получение задачи по ID.
     */
    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Должен вернуть задачу, если она существует")
    void getTaskByIdWhenExistsShouldReturnTask() throws Exception {
        long taskId = 1L;
        TaskResponseDto responseDto = new TaskResponseDto(taskId, "Test Task", "Desc", testInstant, Status.TODO);
        when(taskService.getById(taskId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    /**
     * Тест для GET /api/v1/tasks/{id} для несуществующей задачи.
     * Проверяет, что возвращается статус 404 Not Found.
     */
    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Должен вернуть 404, если задача не найдена")
    void getTaskByIdWhenNotFoundShouldReturn404() throws Exception {
        long taskId = 99L;
        when(taskService.getById(taskId)).thenThrow(new TaskNotFoundException("Task not found"));

        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }

    /**
     * Тест для GET /api/v1/tasks.
     * Проверяет получение списка задач с пагинацией.
     */
    @Test
    @DisplayName("GET /api/v1/tasks - Должен вернуть страницу задач")
    void getTasksShouldReturnPagedTasks() throws Exception {
        List<TaskResponseDto> tasks = Collections.singletonList(
                new TaskResponseDto(1L, "Task 1", "Description 1", testInstant, Status.TODO)
        );
        Page<TaskResponseDto> pagedResponse = new PageImpl<>(tasks, PageRequest.of(0, 10), 1);

        when(taskService.getTasks(any(), any(Pageable.class))).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Task 1"));
    }

    /**
     * Тест для PUT /api/v1/tasks.
     * Проверяет успешное обновление задачи.
     */
    @Test
    @DisplayName("PUT /api/v1/tasks - Должен обновить задачу и вернуть 200 OK")
    void updateTaskWhenValidRequestThenReturn200() throws Exception {
        long taskId = 1L;
        UpdateTaskRequestDto requestDto = new UpdateTaskRequestDto(taskId,"Updated Task", "Updated Description", testInstant, Status.DONE);
        TaskResponseDto responseDto = new TaskResponseDto(taskId, "Updated Task", "Updated Description", testInstant, Status.DONE);

        when(taskService.updateTask(any(UpdateTaskRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    /**
     * Тест для PATCH /api/v1/tasks/{id}/status.
     * Проверяет успешное обновление статуса задачи.
     */
    @Test
    @DisplayName("PATCH /api/v1/tasks/{id}/status - Должен обновить статус и вернуть 200 OK")
    void updateTaskStatusWhenValidRequestThenReturn200() throws Exception {
        long taskId = 1L;
        Status newStatus = Status.IN_PROGRESS;
        TaskResponseDto responseDto = new TaskResponseDto(taskId, "Task", "Description", testInstant, newStatus);

        when(taskService.updateTaskStatus(eq(taskId), eq(newStatus))).thenReturn(responseDto);

        mockMvc.perform(patch("/api/v1/tasks/{id}/status", taskId)
                        .param("status", newStatus.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(newStatus.toString()));
    }

    /**
     * Тест для DELETE /api/v1/tasks/{id}.
     * Проверяет успешное удаление задачи и возврат статуса 204 No Content.
     */
    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - Должен вернуть 204 No Content при успешном удалении")
    void deleteTaskWhenExistsShouldReturn204() throws Exception {
        long taskId = 1L;

        mockMvc.perform(delete("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isNoContent());
    }

    /**
     * Тест для DELETE /api/v1/tasks/{id} для несуществующей задачи.
     * Проверяет, что возвращается статус 404 Not Found.
     */
    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - Должен вернуть 404 при попытке удалить несуществующую задачу")
    void deleteTaskWhenNotFoundShouldReturn404() throws Exception {
        long taskId = 99L;
        doThrow(new TaskNotFoundException("Task not found")).when(taskService).deleteById(taskId);

        mockMvc.perform(delete("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }
}