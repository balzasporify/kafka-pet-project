package com.balza.todoapp.service;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;

import java.util.List;

public interface TaskService {
    TaskResponseDto createTask(CreateTaskRequestDto requestDto);
    TaskResponseDto updateTask(Long id, UpdateTaskRequestDto requestDto);
    TaskResponseDto getById(Long id);
    void deleteById(Long id);
    List<TaskResponseDto> findAll();
    List<TaskResponseDto> findByStatus(String status);
    List<TaskResponseDto> findAllAndSort(String sortBy);
}
