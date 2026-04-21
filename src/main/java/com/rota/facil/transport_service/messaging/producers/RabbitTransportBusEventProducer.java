package com.rota.facil.transport_service.messaging.producers;

import com.rota.facil.transport_service.messaging.dto.send.BusEventSend;
import com.rota.facil.transport_service.messaging.mappers.BusEventMapper;
import com.rota.facil.transport_service.persistence.entities.BusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitTransportBusEventProducer {
    private final RabbitTemplate rabbitTemplate;
    private final BusEventMapper busEventMapper;

    @Value("${rabbitmq.transport.exchange}")
    private String transportExchange;

    @Value("${rabbitmq.bus.deleted.routing.key")
    private String busDeletedRoutingKey;

    public void deleteBusEvent(BusEntity deleteBus) {
        BusEventSend busEventSend = busEventMapper.map(deleteBus);
        rabbitTemplate.convertAndSend(transportExchange, busDeletedRoutingKey, busEventMapper);
    }
}
