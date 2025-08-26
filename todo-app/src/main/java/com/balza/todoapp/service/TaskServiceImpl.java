package com.balza.todoapp.service;

import com.balza.todoapp.entity.Task;
import com.balza.todoapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, Task task) {
        Task existingTask = taskRepository.findById(id).orElseThrow(()-> new RuntimeException("Task not found with id: " + id));

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setStatus(task.getStatus());

        return taskRepository.save(existingTask);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> findByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    public List<Task> findAllAndSort(String sortBy) {
        Sort sort = Sort.by(sortBy);
        return taskRepository.findAll(sort);
    }
}
