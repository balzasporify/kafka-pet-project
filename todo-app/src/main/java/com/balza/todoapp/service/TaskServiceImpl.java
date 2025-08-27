package com.balza.todoapp.service;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.entity.Task;
import com.balza.todoapp.exception.TaskNotFoundException;
import com.balza.todoapp.mapper.TaskMapper;
import com.balza.todoapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponseDto createTask(CreateTaskRequestDto requestDto) {
        log.info("Attempting to create a new task with title: '{}'", requestDto.title());
        Task taskToSave = taskMapper.toEntity(requestDto);
        Task savedTask = taskRepository.save(taskToSave);
        log.debug("Successfully created task with id: {}", savedTask.getId());
        return taskMapper.toDto(savedTask);
    }

    @Override
    public TaskResponseDto updateTask(Long id, UpdateTaskRequestDto requestDto) {
        log.info("Attempting to update task with id: {}", id);
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed. Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });

        taskMapper.updateEntityFromDto(requestDto, existingTask);
        Task savedTask = taskRepository.save(existingTask);
        log.debug("Successfully updated task with id: {}", savedTask.getId());
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getById(Long id) {
        log.info("Fetching task with id: {}", id);
        return taskRepository.findById(id)
                .map(taskMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
    }

    @Override
    public void deleteById(Long id) {
        log.info("Attempting to delete task with id: {}", id);
        if (!taskRepository.existsById(id)) {
            log.error("Delete failed. Task not found with id: {}", id);
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
        log.debug("Successfully deleted task with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAll() {
        log.info("Fetching all tasks");
        return taskMapper.toDtoList(taskRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> findByStatus(String status) {
        log.info("Fetching all tasks with status: {}", status);
        return taskMapper.toDtoList(taskRepository.findByStatus(status));
    }



    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAllAndSort(String sortBy) {
        log.info("Fetching all tasks sorted by: {}", sortBy);
        Sort sort = Sort.by(sortBy);
        return taskMapper.toDtoList(taskRepository.findAll(sort));
    }
}