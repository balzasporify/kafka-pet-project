package com.balza.todoapp.service;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.entity.Task;
import com.balza.todoapp.exception.TaskNotFoundException;
import com.balza.todoapp.mapper.TaskMapper;
import com.balza.todoapp.model.Status;
import com.balza.todoapp.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для TaskServiceImpl")
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private final OffsetDateTime testInstant = OffsetDateTime.parse("2025-09-10T10:00:00Z");

    @Test
    @DisplayName("Должен возвращать задачу по ID, если она существует")
    void getByIdWhenTaskExistsThenReturnsTask() {
        long taskId = 1L;
        Task task = new Task(taskId, "Test Title", "Test Desc", testInstant, Status.TODO);
        TaskResponseDto expectedDto = new TaskResponseDto(taskId, "Test Title", "Test Desc", testInstant, Status.TODO);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(expectedDto);

        TaskResponseDto actualDto = taskService.getById(taskId);

        assertThat(actualDto).isEqualTo(expectedDto);
        verify(taskRepository).findById(taskId);
        verify(taskMapper).toDto(task);
    }

    @Test
    @DisplayName("Должен выбрасывать исключение TaskNotFoundException, если задача по ID не найдена")
    void getByIdWhenTaskNotFoundThenThrowsException() {
        long taskId = 99L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getById(taskId));
        verify(taskRepository).findById(taskId);
        verifyNoInteractions(taskMapper);
    }

    @Test
    @DisplayName("Должен успешно создавать и возвращать новую задачу")
    void createTaskShouldSaveAndReturnTask() {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto("New Task", "Desc", testInstant, Status.TODO);
        Task taskToSave = new Task(null, "New Task", "Desc", testInstant, Status.TODO);
        Task savedTask = new Task(1L, "New Task", "Desc", testInstant, Status.TODO);
        TaskResponseDto expectedDto = new TaskResponseDto(1L, "New Task", "Desc", testInstant, Status.TODO);

        when(taskMapper.toEntity(requestDto)).thenReturn(taskToSave);
        when(taskRepository.save(taskToSave)).thenReturn(savedTask);
        when(taskMapper.toDto(savedTask)).thenReturn(expectedDto);

        TaskResponseDto actualDto = taskService.createTask(requestDto);

        assertThat(actualDto).isEqualTo(expectedDto);
        verify(taskRepository).save(taskToSave);
    }

    @Test
    @DisplayName("Должен успешно обновлять и возвращать задачу, если она существует")
    void updateTaskWhenTaskExistsThenUpdatesAndReturnsTask() {
        long taskId = 1L;
        UpdateTaskRequestDto requestDto = new UpdateTaskRequestDto(taskId, "Updated Title", "Updated Desc", testInstant, Status.DONE);
        Task existingTask = new Task(taskId, "Old Title", "Old Desc", OffsetDateTime.now(), Status.TODO);
        Task savedTask = new Task(taskId, "Updated Title", "Updated Desc", testInstant, Status.DONE);
        TaskResponseDto expectedDto = new TaskResponseDto(taskId, "Updated Title", "Updated Desc", testInstant, Status.DONE);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.toDto(savedTask)).thenReturn(expectedDto);

        TaskResponseDto actualDto = taskService.updateTask(requestDto);

        verify(taskRepository).save(existingTask);
        assertThat(actualDto.title()).isEqualTo("Updated Title");
        assertThat(actualDto.status()).isEqualTo(Status.DONE);
    }

    @Test
    @DisplayName("Должен выбрасывать исключение TaskNotFoundException при попытке обновить несуществующую задачу")
    void updateTaskWhenTaskNotFoundThenThrowsException() {
        long taskId = 99L;
        UpdateTaskRequestDto requestDto = new UpdateTaskRequestDto(taskId, "Title", "Desc", testInstant, Status.TODO);
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(requestDto));
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Должен вызывать метод deleteById")
    void deleteByIdShouldCallDelete() {
        long taskId = 1L;
        doNothing().when(taskRepository).deleteById(taskId);
        taskService.deleteById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    @DisplayName("Должен возвращать страницу задач")
    void getTasksShouldReturnPagedTasks() {
        Pageable pageable = PageRequest.of(0, 10);
        Task task = new Task(1L, "Task 1", "d1", testInstant, Status.TODO);
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task), pageable, 1);
        TaskResponseDto expectedDto = new TaskResponseDto(1L, "Task 1", "d1", testInstant, Status.TODO);

        when(taskRepository.findAll(pageable)).thenReturn(taskPage);
        when(taskMapper.toDto(task)).thenReturn(expectedDto);

        Page<TaskResponseDto> actualPage = taskService.getTasks(null, pageable);

        assertThat(actualPage.getContent()).hasSize(1);
        assertThat(actualPage.getContent().get(0)).isEqualTo(expectedDto);
        verify(taskRepository).findAll(pageable);
        verify(taskMapper).toDto(task);
    }
}