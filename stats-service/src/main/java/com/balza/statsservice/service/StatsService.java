package com.balza.statsservice.service;

import com.balza.statsservice.api.StatsView;
import com.balza.statsservice.events.TaskUpdatedEvent;
import com.balza.statsservice.model.TaskUpdate;
import com.balza.statsservice.repository.TaskUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final TaskUpdateRepository repo;
    private final JdbcTemplate jdbcTemplate;

    @KafkaListener(
            topics = "task.updated",
            containerFactory = "taskUpdatedKafkaListenerContainerFactory"
    )
    public void onTaskUpdated(TaskUpdatedEvent event) {
        repo.save(TaskUpdate.builder()
                .taskId(event.getTaskId())
                .lastUpdated(event.getOccurredAt())
                .build());
        updateTotalTasks(event.getTotalTasks());
    }

    public void updateTotalTasks(Long total) {
        jdbcTemplate.update("DELETE FROM stats.total_tasks");
        jdbcTemplate.update("INSERT INTO stats.total_tasks(total) VALUES (?)", total);
    }

    public Long getTotalTasks() {
        return jdbcTemplate.queryForObject(
                "SELECT total FROM stats.total_tasks LIMIT 1",
                Long.class
        );
    }

    public StatsView getStats() {
        long updated = repo.countUpdatedTasks();
        long total = getTotalTasks();
        double percent = total == 0 ? 0.0 : (updated * 100.0) / total;
        return new StatsView(total, updated, percent);
    }


}
