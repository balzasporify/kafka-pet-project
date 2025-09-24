package com.balza.statsservice.api;

public record StatsView(long totalTasks, long updatedTasks, double updatedPercent) {
}
