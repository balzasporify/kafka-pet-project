package com.balza.todoapp.service;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.entity.Task;
import com.balza.todoapp.events.TaskEventPublisher;
import com.balza.todoapp.events.TaskUpdatedEvent;
import com.balza.todoapp.exception.TaskNotFoundException;
import com.balza.todoapp.mapper.TaskMapper;
import com.balza.todoapp.model.Status;
import com.balza.todoapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskEventPublisher taskEventPublisher;

    @Override
    @Transactional
    public TaskResponseDto createTask(CreateTaskRequestDto requestDto) {
        log.info("Attempting to create a new task with title: '{}'", requestDto.title());
        Task taskToSave = taskMapper.toEntity(requestDto);
        Task savedTask = taskRepository.save(taskToSave);
        log.info("Successfully created task with id: {}", savedTask.getId());
        TaskResponseDto dto = taskMapper.toDto(savedTask);
        return dto;
    }


    @Override
    @Transactional
    public TaskResponseDto updateTask(UpdateTaskRequestDto requestDto) {
        final Long id = requestDto.id();
        log.info("Attempting to update task with id: {}", id);
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed. Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });

        Map<String, Object> prev = Map.of(
                "title", existingTask.getTitle(),
                "decription", existingTask.getDescription(),
                "dueDate", existingTask.getDueDate(),
                "status", existingTask.getStatus()
        );

        existingTask.setTitle(requestDto.title());
        existingTask.setDescription(requestDto.description());
        existingTask.setDueDate(requestDto.dueDate());
        existingTask.setStatus(requestDto.status());

        Task savedTask = taskRepository.save(existingTask);
        log.info("Successfully updated task with id: {}", savedTask.getId());

        Map<String, Object> curr = Map.of(
                "title", savedTask.getTitle(),
                "description", savedTask.getDescription(),
                "dueDate", savedTask.getDueDate(),
                "status", savedTask.getStatus()
        );
        taskEventPublisher.publishUpdated(
                TaskUpdatedEvent.of(savedTask.getId(), prev, curr)
        );

        TaskResponseDto dto = taskMapper.toDto(savedTask);
        return dto;
    }

    @Override
    @Transactional
    public TaskResponseDto updateTaskStatus(Long id, Status status) {
        log.info("Attempting to update status to {} for task with id: {}", status, id);
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update status failed. Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
        existingTask.setStatus(status);
        Task savedTask = taskRepository.save(existingTask);
        log.info("Successfully updated status for task with id: {}", savedTask.getId());
        TaskResponseDto dto = taskMapper.toDto(savedTask);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getById(Long id) {
        log.info("Fetching task with id: {}", id);
        TaskResponseDto taskResponseDto = taskRepository.findById(id)
                .map(taskMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
        return taskResponseDto;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Attempting to delete task with id: {}", id);
        taskRepository.deleteById(id);
        log.info("Delete operation called for task with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDto> getTasks(Status status, Pageable pageable) {
        log.info("Fetching tasks with status: [{}] and sortBy: [{}]", status, pageable);
        Page<Task> taskPage;
        if (status != null) {
            taskPage = taskRepository.findByStatus(status, pageable);
        } else {
            taskPage = taskRepository.findAll(pageable);
        }
        Page<TaskResponseDto> pageDto = taskPage.map(taskMapper::toDto);
        return pageDto;
    }
}