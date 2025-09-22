package com.balza.todoapp.controller;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.model.Status;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.balza.todoapp.util.RestApiUrls.*;

public interface TaskApi {

    @PostMapping(POST_TASK_PATH)
    ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody CreateTaskRequestDto requestDto);

    @GetMapping(GET_TASK_BY_ID_PATH)
    ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id);

    @GetMapping(GET_TASK_PATH)
    ResponseEntity<Page<TaskResponseDto>> getTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    );

    @PutMapping(PUT_TASK_PATH)
    ResponseEntity<TaskResponseDto> updateTask(@Valid @RequestBody UpdateTaskRequestDto requestDto);

    @PatchMapping(PATCH_TASK_PATH)
    ResponseEntity<TaskResponseDto> updateTaskStatus(@PathVariable Long id, @RequestParam Status status);

    @DeleteMapping(DELETE_TASK_PATH)
    ResponseEntity<Void> deleteTask(@PathVariable Long id);
}
