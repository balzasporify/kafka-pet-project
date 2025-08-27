package com.balza.todoapp.controller;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.exception.TaskNotFoundException;
import com.balza.todoapp.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/{id}")
    public TaskResponseDto getTaskById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @PostMapping
    public TaskResponseDto createTask(@RequestBody CreateTaskRequestDto taskToCreate) {
        return taskService.createTask(taskToCreate);
    }

    @PutMapping("/{id}")
    public TaskResponseDto updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequestDto taskToUpdate) {
        return taskService.updateTask(id, taskToUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
    }

    @GetMapping
    public List<TaskResponseDto> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy) {
        if (status != null) {
            return taskService.findByStatus(status);
        }
        if (sortBy != null) {
            return taskService.findAllAndSort(sortBy);
        }
        return taskService.findAll();
    }
}
