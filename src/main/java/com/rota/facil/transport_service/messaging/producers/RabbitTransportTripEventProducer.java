package com.rota.facil.transport_service.messaging.producers;

import com.rota.facil.transport_service.domain.enums.ActionType;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.messaging.dto.send.TripEventSend;
import com.rota.facil.transport_service.messaging.mappers.TripEventMapper;
import com.rota.facil.transport_service.persistence.entities.TripEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RabbitTransportTripEventProducer {
    private final RabbitTemplate rabbitTemplate;
    private final TripEventMapper tripEventMapper;

    @Value("${rabbitmq.transport.exchange}")
    private String transportExchange;


    @Value("${rabbitmq.trip.cancelled.routing.key}")
    private String tripCancelledRoutingKey;

    @Value("${rabbitmq.trip.created.routing.key}")
    private String tripCreatedRoutingKey;

    @Value("${rabbitmq.trip.deleted.routing.key}")
    private String tripDeletedRoutingKey;


    public void createTripEvent(TripEntity createTrip) {
        TripEventSend tripEventSend = tripEventMapper.map(createTrip);
        rabbitTemplate.convertAndSend(transportExchange, tripCreatedRoutingKey, tripEventSend);
    }

    public void cancelTripEvent(TripEntity cancelTrip, CurrentUser currentUser, List<String> emails) {
        String title =  currentUser.email() + ActionType.UPDATE + "viagem para status de cancelado";
        TripEventSend tripEventSend = tripEventMapper.map(cancelTrip, currentUser, ActionType.UPDATE, title, emails);
        rabbitTemplate.convertAndSend(transportExchange, tripCancelledRoutingKey, tripEventSend);

    }
    public void deleteTripEvent(TripEntity deleteTrip, CurrentUser currentUser) {
        TripEventSend tripEventSend = tripEventMapper.map(deleteTrip, currentUser, ActionType.DELETE);
        rabbitTemplate.convertAndSend(transportExchange, tripDeletedRoutingKey, tripEventSend);
    }
}
