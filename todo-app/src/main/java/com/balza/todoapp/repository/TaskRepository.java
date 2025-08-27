package com.balza.todoapp.repository;

import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(String status);
}
