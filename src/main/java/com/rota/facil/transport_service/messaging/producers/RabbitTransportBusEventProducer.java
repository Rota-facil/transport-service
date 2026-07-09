package com.rota.facil.transport_service.messaging.producers;

import com.rota.facil.transport_service.domain.enums.ResourceName;
import com.rota.facil.transport_service.domain.enums.TransportAuditAction;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.messaging.dto.send.BusEventSend;
import com.rota.facil.transport_service.persistence.entities.BusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitTransportBusEventProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.transport.exchange}")
    private String transportExchange;

    @Value("${rabbitmq.bus.created.routing.key}")
    private String busCreatedRoutingKey;

    @Value("${rabbitmq.bus.updated.routing.key}")
    private String busUpdatedRoutingKey;

    @Value("${rabbitmq.bus.deleted.routing.key}")
    private String busDeletedRoutingKey;

    public void createBusEvent(BusEntity bus, CurrentUser currentUser) {
        sendBusAuditEvent(bus, currentUser, TransportAuditAction.BUS_CREATED, busCreatedRoutingKey);
    }

    public void updateBusEvent(BusEntity bus, CurrentUser currentUser) {
        sendBusAuditEvent(bus, currentUser, TransportAuditAction.BUS_UPDATED, busUpdatedRoutingKey);
    }

    public void deleteBusEvent(BusEntity bus, CurrentUser currentUser) {
        sendBusAuditEvent(bus, currentUser, TransportAuditAction.BUS_DELETED, busDeletedRoutingKey);
    }

    private void sendBusAuditEvent(BusEntity bus, CurrentUser currentUser, TransportAuditAction auditAction, String routingKey) {
        BusEventSend eventSend = new BusEventSend(
                bus.getId(),
                currentUser.userId(),
                currentUser.role(),
                currentUser.email(),
                auditAction.title(currentUser.email(), bus.getPlate()),
                auditAction.getActionType(),
                ResourceName.BUS.name(),
                bus.getId(),
                bus.getPlate(),
                bus.getCapacity()
        );

        rabbitTemplate.convertAndSend(transportExchange, routingKey, eventSend);
    }
}
