package com.balza.statsservice.config;

import com.balza.statsservice.events.TaskUpdatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;

    @Bean
    public ConsumerFactory<String, TaskUpdatedEvent> taskUpdatedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "stats-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        JsonDeserializer<TaskUpdatedEvent> valueDeserializer =
                new JsonDeserializer<>(TaskUpdatedEvent.class, false);
        valueDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskUpdatedEvent>
    taskUpdatedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskUpdatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taskUpdatedConsumerFactory());
        return factory;
    }
}
