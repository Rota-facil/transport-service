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
        boardPointRepository.save(boardPointEntity);
    }

    public void update(BoardPointEntity boardPointEntity) {
        BoardPointEntity boardPointFound = this.fetchEntity(boardPointEntity.getId());
        boardPointFound.update(boardPointEntity);
        boardPointRepository.save(boardPointFound);
    }

    public void delete(BoardPointEntity boardPointEntity) {
        boardPointRepository.deleteById(boardPointEntity.getId());
    }

    private BoardPointEntity fetchEntity(UUID boardPointId) {
        return boardPointRepository.findById(boardPointId)
                .orElseThrow(BoardPointNotFoundException::new);
    }
}
