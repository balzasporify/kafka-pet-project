package com.balza.todoapp.dto;

import java.time.LocalDateTime;

public record TaskResponseDto(
        Long id,
        String title,
        String description,
        LocalDateTime dueDate,
        String status
) {
}
