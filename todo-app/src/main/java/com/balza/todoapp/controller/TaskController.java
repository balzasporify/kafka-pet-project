package com.balza.todoapp.controller;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.model.Status;
import com.balza.todoapp.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.balza.todoapp.util.RestApiUrls.TASK_API_BASE_PATH;

@RestController
@RequestMapping(TASK_API_BASE_PATH)
@RequiredArgsConstructor
public class TaskController implements TaskApi {
    private final TaskService taskService;


    @Override
    public ResponseEntity<TaskResponseDto> createTask(CreateTaskRequestDto requestDto) {
        TaskResponseDto createdTask = taskService.createTask(requestDto);
        ResponseEntity<TaskResponseDto> response = new ResponseEntity<>(createdTask, HttpStatus.CREATED);
        return response;
    }

    @Override
    public ResponseEntity<TaskResponseDto> getTaskById(Long id) {
        TaskResponseDto taskDto = taskService.getById(id);
        ResponseEntity<TaskResponseDto> response = ResponseEntity.ok(taskDto);
        return response;
    }

    @Override
    public ResponseEntity<Page<TaskResponseDto>> getTasks(Status status, String sortBy, String sortOrder, Integer page, Integer size) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TaskResponseDto> tasksPage = taskService.getTasks(status, pageable);
        ResponseEntity<Page<TaskResponseDto>> response = ResponseEntity.ok(tasksPage);
        return response;
    }

    @Override
    public ResponseEntity<TaskResponseDto> updateTask(UpdateTaskRequestDto requestDto) {
        TaskResponseDto updatedTask = taskService.updateTask(requestDto);
        ResponseEntity<TaskResponseDto> response = ResponseEntity.ok(updatedTask);
        return response;
    }

    @Override
    public ResponseEntity<TaskResponseDto> updateTaskStatus(Long id, Status status) {
        TaskResponseDto updatedTask = taskService.updateTaskStatus(id, status);
        ResponseEntity<TaskResponseDto> response = ResponseEntity.ok(updatedTask);
        return response;
    }

    @Override
    public ResponseEntity<Void> deleteTask(Long id) {
        taskService.deleteById(id);
        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }
}
