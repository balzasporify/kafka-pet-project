package com.balza.todoapp.controller;

import com.balza.todoapp.entity.Task;
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
    public Task getTaskById(@PathVariable Long id) {
        return taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
    }

    @GetMapping
    public List<Task> getAllTasks(
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
