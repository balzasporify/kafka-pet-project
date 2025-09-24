package com.balza.statsservice.service;

import com.balza.statsservice.api.StatsView;
import com.balza.statsservice.events.TaskUpdatedEvent;
import com.balza.statsservice.model.TaskUpdate;
import com.balza.statsservice.repository.TaskUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final TaskUpdateRepository repo;

    @KafkaListener(
            topics = "task.updated",
            containerFactory = "taskUpdatedKafkaListenerContainerFactory"
    )
    public void onTaskUpdated(TaskUpdatedEvent event) {
        repo.save(TaskUpdate.builder()
                .taskId(event.getTaskId())
                .lastUpdated(event.getOccurredAt())
                .build());
    }

    public StatsView getStats() {
        long total = repo.countAllTasks();
        long updated = repo.countUpdatedTasks();
        double percent = total == 0 ? 0.0 : (updated * 100.0) / total;
        return new StatsView(total, updated, percent);
    }


}
