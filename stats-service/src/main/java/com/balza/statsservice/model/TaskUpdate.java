package com.balza.statsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "task_updates")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUpdate {
    @Id
    private Long taskId;
    private Instant lastUpdated;
}
