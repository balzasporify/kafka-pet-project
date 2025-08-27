package com.balza.todoapp.mapper;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskResponseDto toDto(Task task);
    List<TaskResponseDto> toDtoList(List<Task> tasks);

    Task toEntity(CreateTaskRequestDto requestDto);
    Task toEntity(UpdateTaskRequestDto requestDto);

    void updateEntityFromDto(UpdateTaskRequestDto dto, @MappingTarget Task task);
}
