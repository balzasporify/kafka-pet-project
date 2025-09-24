package com.balza.statsservice.repository;

import com.balza.statsservice.model.TaskUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskUpdateRepository extends JpaRepository<TaskUpdate, Long> {

    @Query(value = "select count(*) from task_updates", nativeQuery = true)
    long countUpdatedTasks();


    @Query(value = "select count(*) from tasks", nativeQuery = true)
    long countAllTasks();
}
