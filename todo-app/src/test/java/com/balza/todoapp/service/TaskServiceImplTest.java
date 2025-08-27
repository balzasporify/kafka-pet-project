package com.balza.todoapp.service;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.entity.Task;
import com.balza.todoapp.exception.TaskNotFoundException;
import com.balza.todoapp.mapper.TaskMapper;
import com.balza.todoapp.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
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

    private final Instant testInstant = Instant.parse("2025-09-10T10:00:00Z");

    @Test
    @DisplayName("Должен возвращать задачу по ID, если она существует")
    void getByIdWhenTaskExistsThenReturnsTask() {
        long taskId = 1L;
        Task task = new Task(taskId, "Test Title", "Test Desc", testInstant, "TODO");
        TaskResponseDto expectedDto = new TaskResponseDto(taskId, "Test Title", "Test Desc", testInstant, "TODO");

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
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto("New Task", "Desc", testInstant, "TODO");
        Task taskToSave = new Task(null, "New Task", "Desc", testInstant, "TODO");
        Task savedTask = new Task(1L, "New Task", "Desc", testInstant, "TODO");
        TaskResponseDto expectedDto = new TaskResponseDto(1L, "New Task", "Desc", testInstant, "TODO");

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
        UpdateTaskRequestDto requestDto = new UpdateTaskRequestDto("Updated Title", "Updated Desc", testInstant, "DONE");
        Task existingTask = new Task(taskId, "Old Title", "Old Desc", Instant.now(), "TODO");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);
        when(taskMapper.toDto(existingTask)).thenReturn(new TaskResponseDto(taskId, "Updated Title", "Updated Desc", testInstant, "DONE"));

        TaskResponseDto actualDto = taskService.updateTask(taskId, requestDto);

        verify(taskMapper).updateEntityFromDto(requestDto, existingTask);
        verify(taskRepository).save(existingTask);
        assertThat(actualDto.title()).isEqualTo("Updated Title");
        assertThat(actualDto.status()).isEqualTo("DONE");
    }

    @Test
    @DisplayName("Должен выбрасывать исключение TaskNotFoundException при попытке обновить несуществующую задачу")
    void updateTaskWhenTaskNotFoundThenThrowsException() {
        long taskId = 99L;
        UpdateTaskRequestDto requestDto = new UpdateTaskRequestDto("Title", "Desc", testInstant, "TODO");
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(taskId, requestDto));
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Должен вызывать метод deleteById, если задача существует")
    void deleteByIdWhenTaskExistsThenDeletesTask() {
        long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId);

        taskService.deleteById(taskId);

        verify(taskRepository).existsById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    @DisplayName("Должен выбрасывать исключение TaskNotFoundException при попытке удалить несуществующую задачу")
    void deleteByIdWhenTaskNotFoundThenThrowsException() {
        long taskId = 99L;
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteById(taskId));
        verify(taskRepository).existsById(taskId);
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Должен возвращать список задач")
    void findAllShouldReturnListOfTasks() {
        List<Task> tasks = List.of(new Task(1L, "Task 1", "d1", testInstant, "TODO"));
        List<TaskResponseDto> expectedDtos = List.of(new TaskResponseDto(1L, "Task 1", "d1", testInstant, "TODO"));

        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toDtoList(tasks)).thenReturn(expectedDtos);

        List<TaskResponseDto> actualDtos = taskService.findAll();

        assertThat(actualDtos).isEqualTo(expectedDtos);
    }

    @Test
    @DisplayName("Должен возвращать пустой список, если задач нет")
    void findAllShouldReturnEmptyListWhenNoTasksExist() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());
        when(taskMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<TaskResponseDto> actualDtos = taskService.findAll();

        assertThat(actualDtos).isEmpty();
    }

    @Test
    @DisplayName("Должен возвращать отфильтрованный по статусу список задач")
    void findByStatusShouldReturnFilteredList() {
        String status = "DONE";
        List<Task> tasks = List.of(new Task(2L, "Task 2", "d2", testInstant, "DONE"));
        List<TaskResponseDto> expectedDtos = List.of(new TaskResponseDto(2L, "Task 2", "d2", testInstant, "DONE"));

        when(taskRepository.findByStatus(status)).thenReturn(tasks);
        when(taskMapper.toDtoList(tasks)).thenReturn(expectedDtos);

        List<TaskResponseDto> actualDtos = taskService.findByStatus(status);

        assertThat(actualDtos).isEqualTo(expectedDtos);
        verify(taskRepository).findByStatus(status);
    }

    @Test
    @DisplayName("Должен возвращать отсортированный список задач")
    void findAllAndSortShouldReturnSortedList() {
        String sortByField = "dueDate";
        List<Task> tasks = List.of(new Task());
        List<TaskResponseDto> expectedDtos = List.of(new TaskResponseDto(1L, "T", "D", testInstant, "S"));

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);

        when(taskRepository.findAll(sortCaptor.capture())).thenReturn(tasks);
        when(taskMapper.toDtoList(tasks)).thenReturn(expectedDtos);

        List<TaskResponseDto> actualDtos = taskService.findAllAndSort(sortByField);

        assertThat(actualDtos).isEqualTo(expectedDtos);

        Sort capturedSort = sortCaptor.getValue();
        assertThat(capturedSort.getOrderFor("dueDate")).isNotNull();
    }
}