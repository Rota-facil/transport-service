package com.rota.facil.transport_service.messaging.consumers;

import com.rota.facil.transport_service.business.BoardPointService;
import com.rota.facil.transport_service.business.InstitutionService;
import com.rota.facil.transport_service.messaging.dto.receive.BoardPointEventReceive;
import com.rota.facil.transport_service.messaging.dto.receive.InstitutionEventReceive;
import com.rota.facil.transport_service.messaging.mappers.BoardPointEventMapper;
import com.rota.facil.transport_service.messaging.mappers.InstitutionEventMapper;
import com.rota.facil.transport_service.persistence.entities.BoardPointEntity;
import com.rota.facil.transport_service.persistence.entities.InstitutionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitPlacesEventConsumer {
    private final InstitutionService institutionService;
    private final BoardPointService boardPointService;
    private final InstitutionEventMapper institutionEventMapper;
    private final BoardPointEventMapper boardPointEventMapper;

    @RabbitListener(queues = "${rabbitmq.transport.institution.created.queue}")
    public void handlerInstitutionCreated(InstitutionEventReceive createInstitution) {
        InstitutionEntity institutionEntity = institutionEventMapper.map(createInstitution);
        institutionService.register(institutionEntity);
    }

    @RabbitListener(queues = "${rabbitmq.transport.institution.updated.queue}")
    public void handlerInstitutionUpdated(InstitutionEventReceive updateInstitution) {
        InstitutionEntity institutionEntity = institutionEventMapper.map(updateInstitution);
        institutionService.update(institutionEntity);
    }

    @RabbitListener(queues = "${rabbitmq.transport.institution.deleted.queue}")
    public void handlerInstitutionDeleted(InstitutionEventReceive deleteInstitution) {
        InstitutionEntity institutionEntity = institutionEventMapper.map(deleteInstitution);
        institutionService.delete(institutionEntity);
    }


    @RabbitListener(queues = "${rabbitmq.transport.board.point.created.queue}")
    public void handlerBoardPointCreated(BoardPointEventReceive createBoardPoint) {
        BoardPointEntity boardPointEntity = boardPointEventMapper.map(createBoardPoint);
        boardPointService.register(boardPointEntity);
    }

    @RabbitListener(queues = "${rabbitmq.transport.board.point.updated.queue}")
    public void handlerBoardPointUpdated(BoardPointEventReceive updateBoardPoint) {
        BoardPointEntity boardPointEntity = boardPointEventMapper.map(updateBoardPoint);
        boardPointService.update(boardPointEntity);
    }

    @RabbitListener(queues = "${rabbitmq.transport.board.point.deleted.queue}")
    public void handlerBoardPointDeleted(BoardPointEventReceive deleteBoardPoint) {
        BoardPointEntity boardPointEntity = boardPointEventMapper.map(deleteBoardPoint);
        boardPointService.delete(boardPointEntity);
    }
}
