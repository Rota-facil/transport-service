package com.rota.facil.transport_service.messaging.producers;

import com.rota.facil.transport_service.messaging.dto.send.FeedbackUserEventSend;
import com.rota.facil.transport_service.messaging.dto.send.user.CompleteTripUserEventSend;
import com.rota.facil.transport_service.messaging.dto.send.user.UpdateTripsUserEventSend;
import com.rota.facil.transport_service.messaging.mappers.UserEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @Value("${rabbitmq.user.trip.completed.routing.key}")
    private String completeUserTripRoutingKey;

    @Value("${rabbitmq.user.trips.increased.routing.key}")
    private String increaseUserTripsRoutingKey;

    @Value("${rabbitmq.user.trips.decreased.routing.key}")
    private String decreaseUserTripsRoutingKey;

    public void feedbackUser(UUID userToEvaluateId, double newMediaNote) {
        FeedbackUserEventSend feedbackUserEventSend = userEventMapper.map(userToEvaluateId, newMediaNote);
        rabbitTemplate.convertAndSend(transportExchange, userFeedbackRoutingKey, feedbackUserEventSend);
    }

    public void completeTripUser(List<UUID> userIds) {
        rabbitTemplate.convertAndSend(transportExchange, completeUserTripRoutingKey, new CompleteTripUserEventSend(userIds));
    }

    public void increaseTripsUser(List<UUID> userIds) {
        rabbitTemplate.convertAndSend(transportExchange, increaseUserTripsRoutingKey, new UpdateTripsUserEventSend(userIds));
    }

    public void decreaseTripsUser(List<UUID> userIds) {
        rabbitTemplate.convertAndSend(transportExchange, decreaseUserTripsRoutingKey, new UpdateTripsUserEventSend(userIds));
    }
}
