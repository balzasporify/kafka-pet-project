package com.balza.todoapp.service;

import com.balza.todoapp.entity.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task createTask(Task task);
    Task updateTask(Long id, Task task);
    Optional<Task> findById(Long id);
    void deleteById(Long id);
    List<Task> findAll();
    List<Task> findByStatus(String status);
    List<Task> findAllAndSort(String sortBy);
}
