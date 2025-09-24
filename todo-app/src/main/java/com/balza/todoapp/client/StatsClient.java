package com.balza.todoapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "stats-service", url = "http://localhost:8082")
public interface StatsClient {

    @GetMapping("/api/stats")
    StatsView getStats();
}
