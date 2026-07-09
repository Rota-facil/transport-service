package com.rota.facil.transport_service.messaging.producers;

import com.rota.facil.transport_service.domain.enums.ResourceName;
import com.rota.facil.transport_service.domain.enums.TransportAuditAction;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.messaging.dto.send.trip.TripCancelledEventSend;
import com.rota.facil.transport_service.messaging.dto.send.trip.TripCreatedEventSend;
import com.rota.facil.transport_service.messaging.dto.send.trip.TripDeletedEventSend;
import com.rota.facil.transport_service.messaging.dto.send.trip.TripRunningEventSend;
import com.rota.facil.transport_service.persistence.dto.StudentPersistenceDTO;
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

    @Value("${rabbitmq.transport.exchange}")
    private String transportExchange;

    @Value("${rabbitmq.trip.cancelled.routing.key}")
    private String tripCancelledRoutingKey;

    @Value("${rabbitmq.trip.created.routing.key}")
    private String tripCreatedRoutingKey;

    @Value("${rabbitmq.trip.deleted.routing.key}")
    private String tripDeletedRoutingKey;

    @Value("${rabbitmq.trip.running.routing.key}")
    private String tripRunningRoutingKey;

    public void createTripEvent(TripEntity createTrip) {
        TripCreatedEventSend eventSend = new TripCreatedEventSend(
                createTrip.getId(),
                createTrip.getPrefectureId(),
                createTrip.getRoute() != null ? createTrip.getRoute().getId() : null,
                createTrip.getRoute() != null ? createTrip.getRoute().getName() : null
        );

        rabbitTemplate.convertAndSend(transportExchange, tripCreatedRoutingKey, eventSend);
    }

    public void runningTripEvent(TripEntity runningTrip, CurrentUser currentUser, List<StudentPersistenceDTO> studentsInfo) {
        TransportAuditAction auditAction = TransportAuditAction.TRIP_RUNNING;
        String routeName = runningTrip.getRoute() != null ? runningTrip.getRoute().getName() : runningTrip.getName();
        TripRunningEventSend eventSend = new TripRunningEventSend(
                currentUser.userId(),
                currentUser.role(),
                currentUser.email(),
                auditAction.title(currentUser.email(), routeName),
                auditAction.getActionType(),
                ResourceName.TRIP.name(),
                runningTrip.getId(),
                currentUser.prefectureId(),
                currentUser.userId(),
                currentUser.email(),
                runningTrip.getId(),
                routeName,
                runningTrip.getLatitude() != null ? runningTrip.getLatitude().toString() : null,
                runningTrip.getLongitude() != null ? runningTrip.getLongitude().toString() : null,
                studentsInfo
        );

        rabbitTemplate.convertAndSend(transportExchange, tripRunningRoutingKey, eventSend);
    }

    public void cancelTripEvent(TripEntity cancelTrip, CurrentUser currentUser, List<StudentPersistenceDTO> studentsInfo) {
        TransportAuditAction auditAction = TransportAuditAction.TRIP_CANCELLED;
        String routeName = cancelTrip.getRoute() != null ? cancelTrip.getRoute().getName() : cancelTrip.getName();
        TripCancelledEventSend eventSend = new TripCancelledEventSend(
                currentUser.userId(),
                currentUser.role(),
                currentUser.email(),
                auditAction.title(currentUser.email(), routeName),
                auditAction.getActionType(),
                ResourceName.TRIP.name(),
                cancelTrip.getId(),
                currentUser.prefectureId(),
                currentUser.userId(),
                currentUser.email(),
                cancelTrip.getId(),
                routeName,
                cancelTrip.getReasonOfCancellation(),
                cancelTrip.getLatitude() != null ? cancelTrip.getLatitude().toString() : null,
                cancelTrip.getLongitude() != null ? cancelTrip.getLongitude().toString() : null,
                studentsInfo
        );

        rabbitTemplate.convertAndSend(transportExchange, tripCancelledRoutingKey, eventSend);
    }

    public void deleteTripEvent(TripEntity deleteTrip, CurrentUser currentUser) {
        TransportAuditAction auditAction = TransportAuditAction.TRIP_DELETED;
        String routeName = deleteTrip.getRoute() != null ? deleteTrip.getRoute().getName() : deleteTrip.getName();
        TripDeletedEventSend eventSend = new TripDeletedEventSend(
                currentUser.userId(),
                currentUser.role(),
                currentUser.email(),
                auditAction.title(currentUser.email(), routeName),
                auditAction.getActionType(),
                ResourceName.TRIP.name(),
                deleteTrip.getId(),
                currentUser.prefectureId(),
                currentUser.userId(),
                currentUser.email(),
                deleteTrip.getId(),
                routeName,
                deleteTrip.getReasonOfCancellation(),
                deleteTrip.getLatitude() != null ? deleteTrip.getLatitude().toString() : null,
                deleteTrip.getLongitude() != null ? deleteTrip.getLongitude().toString() : null,
                null
        );

        rabbitTemplate.convertAndSend(transportExchange, tripDeletedRoutingKey, eventSend);
    }
}
