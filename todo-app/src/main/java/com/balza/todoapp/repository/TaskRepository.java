package com.balza.todoapp.repository;

import com.balza.todoapp.entity.Task;
import com.balza.todoapp.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByStatus(Status status, Pageable pageable);
}
