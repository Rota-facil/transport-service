package com.rota.facil.transport_service.messaging.producers;

import com.rota.facil.transport_service.domain.enums.ResourceName;
import com.rota.facil.transport_service.domain.enums.TransportAuditAction;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.messaging.dto.send.RouteCreatedEventSend;
import com.rota.facil.transport_service.messaging.dto.send.RouteDeletedEventSend;
import com.rota.facil.transport_service.messaging.dto.send.RouteUpdatedEventSend;
import com.rota.facil.transport_service.persistence.entities.RouteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitTransportRouteEventProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.transport.exchange}")
    private String transportExchange;

    @Value("${rabbitmq.route.created.routing.key}")
    private String routeCreatedRoutingKey;

    @Value("${rabbitmq.route.updated.routing.key}")
    private String routeUpdatedRoutingKey;

    @Value("${rabbitmq.route.deleted.routing.key}")
    private String routeDeletedRoutingKey;

    public void createRoute(RouteEntity createRoute, CurrentUser currentUser) {
        TransportAuditAction auditAction = TransportAuditAction.ROUTE_CREATED;
        RouteCreatedEventSend eventSend = new RouteCreatedEventSend(
                currentUser.userId(),
                currentUser.prefectureId(),
                currentUser.role(),
                currentUser.email(),
                auditAction.title(currentUser.email(), createRoute.getName()),
                auditAction.getActionType(),
                ResourceName.ROUTE.name(),
                createRoute.getId(),
                createRoute.getId(),
                createRoute.getName()
        );

        rabbitTemplate.convertAndSend(transportExchange, routeCreatedRoutingKey, eventSend);
    }

    public void updateRoute(RouteEntity updateRoute, CurrentUser currentUser) {
        TransportAuditAction auditAction = TransportAuditAction.ROUTE_UPDATED;
        RouteUpdatedEventSend eventSend = new RouteUpdatedEventSend(
                currentUser.userId(),
                currentUser.prefectureId(),
                currentUser.role(),
                currentUser.email(),
                auditAction.title(currentUser.email(), updateRoute.getName()),
                auditAction.getActionType(),
                ResourceName.ROUTE.name(),
                updateRoute.getId(),
                updateRoute.getId(),
                updateRoute.getName()
        );

        rabbitTemplate.convertAndSend(transportExchange, routeUpdatedRoutingKey, eventSend);
    }

    public void deleteRoute(RouteEntity deleteRoute, CurrentUser currentUser) {
        TransportAuditAction auditAction = TransportAuditAction.ROUTE_DELETED;
        RouteDeletedEventSend eventSend = new RouteDeletedEventSend(
                currentUser.userId(),
                currentUser.prefectureId(),
                currentUser.role(),
                currentUser.email(),
                auditAction.title(currentUser.email(), deleteRoute.getName()),
                auditAction.getActionType(),
                ResourceName.ROUTE.name(),
                deleteRoute.getId(),
                deleteRoute.getId(),
                deleteRoute.getName()
        );

        rabbitTemplate.convertAndSend(transportExchange, routeDeletedRoutingKey, eventSend);
    }
}
