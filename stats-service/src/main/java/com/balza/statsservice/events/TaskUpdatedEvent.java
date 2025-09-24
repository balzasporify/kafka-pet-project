package com.balza.statsservice.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskUpdatedEvent {
    private Long taskId;
    private Instant occurredAt;
    private Long totalTasks;
}
