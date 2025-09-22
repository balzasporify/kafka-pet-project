package com.balza.todoapp.mapper;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "status", target = "status")
    TaskResponseDto toDto(Task task);

    @Mapping(target = "id", ignore = true)
    Task toEntity(CreateTaskRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    Task toEntity(UpdateTaskRequestDto requestDto);
}
