package com.shopsphere.notification.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaReliabilityTopicConfig {

    @Bean
    public NewTopic orderEventsTopic(
            @Value("${shopsphere.kafka.topics.orders}") String topicName
    ) {
        return TopicBuilder.name(topicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderEventsDeadLetterTopic(
            @Value("${shopsphere.kafka.topics.orders}") String topicName
    ) {
        return TopicBuilder.name(topicName + "-dlt")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
