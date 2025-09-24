package com.balza.todoapp.events;

import lombok.*;
import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUpdatedEvent {

    @Builder.Default
    private String eventType = "TASK_UPDATED";

    private Long taskId;
    private Long totalTasks;
    private Map<String, Object> prev;
    private Map<String, Object> curr;

    @Builder.Default
    private Instant occurredAt = Instant.now();

    @Builder.Default
    private String producer = "todo-app";

    @Builder.Default
    private int version = 1;

    public static TaskUpdatedEvent of(Long taskId, Long totalTasks, Map<String, Object> prev, Map<String, Object> curr) {
        return TaskUpdatedEvent.builder()
                .taskId(taskId)
                .totalTasks(totalTasks)
                .prev(prev)
                .curr(curr)
                .build();
    }
}
