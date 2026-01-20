package ru.itwizardry.userservice.kafka.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.itwizardry.userservice.kafka.dto.UserEventDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void publishUserCreated(String email) {
        publish("CREATED", email);
    }

    public void publishUserDeleted(String email) {
        publish("DELETED", email);
    }

    private void publish(String operation, String email) {
        UserEventDto event = new UserEventDto(operation, email);
        kafkaTemplate.send(topic, email, event);
        log.info("User event sent: {} {}", operation, email);
    }
}
