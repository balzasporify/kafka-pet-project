package com.balza.todoapp.dto;

import java.time.LocalDateTime;

public record UpdateTaskRequestDto(
        String title,
        String description,
        LocalDateTime dueDate,
        String status
) {
}
