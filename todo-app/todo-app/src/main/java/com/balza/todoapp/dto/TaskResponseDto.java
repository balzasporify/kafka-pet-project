package com.balza.todoapp.dto;

import com.balza.todoapp.model.Status;

import java.time.OffsetDateTime;

public record TaskResponseDto(
        Long id,
        String title,
        String description,
        OffsetDateTime dueDate,
        Status status
) {
}
