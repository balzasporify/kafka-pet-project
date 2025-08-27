package com.balza.todoapp.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTaskRequestDto(
        @NotEmpty(message = "Название не может быть пустым")
        @Size(max = 255, message = "Название слишком длинное")
        String title,
        String description,
        @FutureOrPresent(message = "Срок выполнения не может быть в прошлом")
        LocalDateTime dueDate,
        @NotEmpty(message = "Статус не может быть пустым")
        String status
){}
