package com.balza.todoapp.dto;

import java.time.Instant;

public record TaskResponseDto(
        Long id,
        String title,
        String description,
        Instant dueDate,
        String status
) {
}
