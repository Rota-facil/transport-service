package com.rota.facil.transport_service.messaging.producers;

import com.rota.facil.transport_service.domain.enums.ActionType;
import com.rota.facil.transport_service.http.dto.request.CurrentUser;
import com.rota.facil.transport_service.messaging.dto.send.RouteEventSend;
import com.rota.facil.transport_service.messaging.mappers.RouteEventMapper;
import com.rota.facil.transport_service.persistence.entities.RouteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitTransportRouteEventProducer {
    private final RabbitTemplate rabbitTemplate;
    private final RouteEventMapper routeEventMapper;

    @Value("${rabbitmq.transport.exchange}")
    private String transportExchange;

    @Value("${rabbitmq.route.created.routing.key}")
    private String routeCreatedRoutingKey;

    @Value("${rabbitmq.route.updated.routing.key}")
    private String routeUpdatedRoutingKey;

    @Value("${rabbitmq.route.deleted.routing.key}")
    private String routeDeletedRoutingKey;

    public void createRoute(RouteEntity createRoute, CurrentUser currentUser) {
        RouteEventSend routeEventSend = routeEventMapper.map(createRoute, currentUser, ActionType.CREATE);
        rabbitTemplate.convertAndSend(transportExchange, routeCreatedRoutingKey, routeEventSend);
    }

    public void updateRoute(RouteEntity updateRoute, CurrentUser currentUser) {
        RouteEventSend routeEventSend = routeEventMapper.map(updateRoute, currentUser, ActionType.UPDATE);
        rabbitTemplate.convertAndSend(transportExchange, routeUpdatedRoutingKey, routeEventSend);
    }

    public void deleteRoute(RouteEntity deleteRoute, CurrentUser currentUser) {
        RouteEventSend routeEventSend = routeEventMapper.map(deleteRoute, currentUser, ActionType.DELETE);
        rabbitTemplate.convertAndSend(transportExchange, routeDeletedRoutingKey, routeEventSend);
    }
}
