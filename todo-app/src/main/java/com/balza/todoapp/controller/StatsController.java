package com.balza.todoapp.controller;

import com.balza.todoapp.client.StatsClient;
import com.balza.todoapp.client.StatsView;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsClient statsClient;

    @GetMapping("/stats/remote")
    public StatsView getRemoteStats() {
        return statsClient.getStats();
    }
}
