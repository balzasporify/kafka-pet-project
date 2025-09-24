package com.balza.todoapp.events;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskEventPublisher {
    public static final String TOPIC_UPDATED = "task.updated";
    private final KafkaTemplate<String, TaskUpdatedEvent> template;

    public TaskEventPublisher(KafkaTemplate<String, TaskUpdatedEvent> template) {
        this.template = template;
    }

    public void publishUpdated(TaskUpdatedEvent event) {
        String key = String.valueOf(event.getTaskId());
        template.send(TOPIC_UPDATED, key, event);
    }
}
