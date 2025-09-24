package com.balza.todoapp.client;

import lombok.Data;

@Data
public class StatsView {
    private Long totalTasks;
    private Long updatedTasks;
    private Double updatedPercentage;
}
