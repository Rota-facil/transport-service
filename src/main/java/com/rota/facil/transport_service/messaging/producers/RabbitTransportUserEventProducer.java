package com.rota.facil.transport_service.messaging.producers;

import com.rota.facil.transport_service.messaging.dto.send.FeedbackUserEventSend;
import com.rota.facil.transport_service.messaging.mappers.UserEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RabbitTransportUserEventProducer {
    private final RabbitTemplate rabbitTemplate;

    private final UserEventMapper userEventMapper;

    @Value("${rabbitmq.transport.exchange}")
    private String transportExchange;

    @Value("${rabbitmq.user.feedback.routing.key}")
    private String userFeedbackRoutingKey;

    public void feedbackUser(UUID userToEvaluateId, double newMediaNote) {
        FeedbackUserEventSend feedbackUserEventSend = userEventMapper.map(userToEvaluateId, newMediaNote);
        rabbitTemplate.convertAndSend(transportExchange, userFeedbackRoutingKey, feedbackUserEventSend);
    }
}
