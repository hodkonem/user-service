package ru.itwizardry.userservice.kafka.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.itwizardry.userservice.kafka.dto.UserEventDto;
import ru.itwizardry.userservice.kafka.dto.UserOperation;

import static ru.itwizardry.userservice.kafka.dto.UserOperation.CREATED;
import static ru.itwizardry.userservice.kafka.dto.UserOperation.DELETED;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic}")
    private String topic;

    public void publishUserCreated(String email) {
        publish(CREATED, email);
    }

    public void publishUserDeleted(String email) {
        publish(DELETED, email);
    }

    private void publish(UserOperation operation, String email) {
        UserEventDto event = new UserEventDto(operation, email);

        final String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize UserEventDto to JSON", e);
        }

        log.debug("Publishing user event [operation={}, email={}, topic={}]", operation, email, topic);

        kafkaTemplate.send(topic, email, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send user event [operation={}, email={}, topic={}]",
                                operation, email, topic, ex);
                        return;
                    }
                    var meta = result.getRecordMetadata();
                    log.info("User event sent [operation={}, email={}, topic={}, partition={}, offset={}]",
                            operation, email, topic, meta.partition(), meta.offset());
                });
    }
}
