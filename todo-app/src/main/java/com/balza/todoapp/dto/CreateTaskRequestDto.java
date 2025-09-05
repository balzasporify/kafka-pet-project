package com.balza.todoapp.dto;

import com.balza.todoapp.model.Status;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record CreateTaskRequestDto(
        @NotEmpty(message = "{task.title.notEmpty}")
        @Size(max = 255, message = "{task.title.size}")
        String title,
        String description,
        @FutureOrPresent(message = "{task.dueDate.futureOrPresent}")
        OffsetDateTime dueDate,
        @NotNull(message = "{task.status.notNull}")
        Status status
){}
