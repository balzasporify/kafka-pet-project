package com.balza.todoapp.service;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.entity.Task;
import com.balza.todoapp.exception.TaskNotFoundException;
import com.balza.todoapp.mapper.TaskMapper;
import com.balza.todoapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponseDto createTask(CreateTaskRequestDto requestDto) {
        Task taskToSave = taskMapper.toEntity(requestDto);
        Task savedTask = taskRepository.save(taskToSave);
        return taskMapper.toDto(savedTask);
    }

    @Override
    public TaskResponseDto updateTask(Long id, UpdateTaskRequestDto requestDto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        taskMapper.updateEntityFromDto(requestDto, existingTask);
        Task savedTask = taskRepository.save(existingTask);
        return taskMapper.toDto(savedTask);
    }

    @Override
    public TaskResponseDto getById(Long id) {
        return taskRepository.findById(id)
                .map(taskMapper::toDto)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public List<TaskResponseDto> findAll() {
        return taskMapper.toDtoList(taskRepository.findAll());
    }

    @Override
    public List<TaskResponseDto> findByStatus(String status) {
        return taskMapper.toDtoList(taskRepository.findByStatus(status));
    }

    @Override
    public List<TaskResponseDto> findAllAndSort(String sortBy) {
        Sort sort = Sort.by(sortBy);
        return taskMapper.toDtoList(taskRepository.findAll(sort));
    }
}
