package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.exceptions.BoardPointNotFoundException;
import com.rota.facil.transport_service.persistence.entities.BoardPointEntity;
import com.rota.facil.transport_service.persistence.repositories.BoardPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardPointService {
    private final BoardPointRepository boardPointRepository;

    public void register(BoardPointEntity boardPointEntity) {
        boardPointEntity.setDeleted(false);
        boardPointEntity.setGeom();
        boardPointRepository.save(boardPointEntity);
    }

    public void update(BoardPointEntity boardPointEntity) {
        BoardPointEntity boardPointFound = this.fetchEntity(boardPointEntity.getId());
        if (boardPointFound.getDeleted()) return;
        boardPointFound.update(boardPointEntity);
        boardPointRepository.save(boardPointFound);
    }

    public void delete(BoardPointEntity boardPointEntity) {
        BoardPointEntity boardPointFound = this.fetchEntity(boardPointEntity.getId());
        boardPointFound.markAsDeleted();
        boardPointRepository.save(boardPointFound);
    }

    private BoardPointEntity fetchEntity(UUID boardPointId) {
        return boardPointRepository.findById(boardPointId)
                .orElseThrow(BoardPointNotFoundException::new);
    }
}
