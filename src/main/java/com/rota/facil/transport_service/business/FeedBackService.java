package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.exceptions.InvalidTypeUserException;
import com.rota.facil.transport_service.domain.exceptions.UserNotFoundException;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.request.user.EvaluateUserRequestDTO;
import com.rota.facil.transport_service.http.dto.response.user.EvaluateUserResponseDTO;
import com.rota.facil.transport_service.persistence.entities.FeedBackEntity;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import com.rota.facil.transport_service.persistence.mappers.FeedBackMapper;
import com.rota.facil.transport_service.persistence.repositories.FeedBackRepository;
import com.rota.facil.transport_service.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedBackService {
    private final UserRepository userRepository;
    private final FeedBackRepository feedBackRepository;
    private final FeedBackMapper feedBackMapper;

    @Transactional
    public EvaluateUserResponseDTO evaluate(CurrentUser currentUser, UUID userToEvaluateId, EvaluateUserRequestDTO request) {
        UserEntity userToEvaluateFound = userRepository.findById(userToEvaluateId)
                .orElseThrow(UserNotFoundException::new);
        UserEntity currentUserFound = userRepository.findById(currentUser.userId())
                .orElseThrow(UserNotFoundException::new);

        this.verifyTypeUserToEvaluation(currentUser, userToEvaluateFound);

        double newMediaNote = feedBackRepository.calculateMediaNoteByUserId(userToEvaluateId);

        FeedBackEntity preSaved = feedBackMapper.map(request, currentUserFound, userToEvaluateFound);
        FeedBackEntity saved = feedBackRepository.save(preSaved);

        userToEvaluateFound.setScore(newMediaNote);
        userRepository.save(userToEvaluateFound);

        return feedBackMapper.map(saved);
    }

    private void verifyTypeUserToEvaluation(CurrentUser currentUser, UserEntity userToEvaluate) {
        if (currentUser.isStudent() && userToEvaluate.isNotDriver()) throw new InvalidTypeUserException("Só é possível avaliar motoristas");
        if (currentUser.isDriver() && userToEvaluate.isNotStudent()) throw new InvalidTypeUserException("Só é possível avaliar estudantes");
        throw new InvalidTypeUserException("Apenas alunos e motoristas podem avaliar outros alunos e motoristas");
    }

}
