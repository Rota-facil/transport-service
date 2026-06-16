package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.exceptions.InvalidTypeUserException;
import com.rota.facil.transport_service.domain.exceptions.UserAlreadyEvaluateInThisTripException;
import com.rota.facil.transport_service.domain.exceptions.UserNotFoundException;
import com.rota.facil.transport_service.domain.exceptions.UserOfTripNotFoundException;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.request.user.EvaluateUserRequestDTO;
import com.rota.facil.transport_service.http.dto.response.user.EvaluateUserResponseDTO;
import com.rota.facil.transport_service.messaging.producers.RabbitTransportUserEventProducer;
import com.rota.facil.transport_service.persistence.entities.FeedBackEntity;
import com.rota.facil.transport_service.persistence.entities.TripUserEntity;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import com.rota.facil.transport_service.persistence.mappers.FeedBackMapper;
import com.rota.facil.transport_service.persistence.repositories.FeedBackRepository;
import com.rota.facil.transport_service.persistence.repositories.TripUserRepository;
import com.rota.facil.transport_service.persistence.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedBackService {
    private final UserRepository userRepository;
    private final FeedBackRepository feedBackRepository;
    private final TripUserRepository tripUserRepository;
    private final FeedBackMapper feedBackMapper;
    private final RabbitTransportUserEventProducer userEventProducer;

    @Transactional
    public EvaluateUserResponseDTO evaluateByTrip(CurrentUser currentUser, UUID userId, UUID tripId, EvaluateUserRequestDTO request) {
        TripUserEntity userOfTrip = tripUserRepository.findByTripIdAndUserId(tripId, userId)
                .orElseThrow(UserOfTripNotFoundException::new);

        if (!userOfTrip.getScore().equals(0.0)) throw new UserAlreadyEvaluateInThisTripException();

        EvaluateUserResponseDTO evaluation = this.evaluate(currentUser, userId, request);

        userOfTrip.setScore(request.note());
        tripUserRepository.save(userOfTrip);
        return evaluation;
    }

    @Transactional
    public EvaluateUserResponseDTO evaluate(CurrentUser currentUser, UUID userToEvaluateId, EvaluateUserRequestDTO request) {
        UserEntity userToEvaluateFound = userRepository.findById(userToEvaluateId)
                .orElseThrow(UserNotFoundException::new);
        UserEntity currentUserFound = userRepository.findById(currentUser.userId())
                .orElseThrow(UserNotFoundException::new);

        this.verifyTypeUserToEvaluation(currentUser, userToEvaluateFound);

        FeedBackEntity preSaved = feedBackMapper.map(request, currentUserFound, userToEvaluateFound);
        FeedBackEntity saved = feedBackRepository.save(preSaved);

        double newMediaNote = feedBackRepository.calculateMediaNoteByUserId(userToEvaluateId);


        userToEvaluateFound.setScore(newMediaNote);
        userRepository.save(userToEvaluateFound);

        userEventProducer.feedbackUser(userToEvaluateId, newMediaNote);
        return feedBackMapper.map(saved);
    }

    private void verifyTypeUserToEvaluation(CurrentUser currentUser, UserEntity userToEvaluate) {
        if (currentUser.isStudent() && userToEvaluate.isNotDriver()) throw new InvalidTypeUserException("Só é possível avaliar motoristas");
        if (currentUser.isDriver() && userToEvaluate.isNotStudent()) throw new InvalidTypeUserException("Só é possível avaliar estudantes");
        throw new InvalidTypeUserException("Apenas alunos e motoristas podem avaliar outros alunos e motoristas");
    }
}
