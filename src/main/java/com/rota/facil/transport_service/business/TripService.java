package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.exceptions.*;
import com.rota.facil.transport_service.http.dto.request.trip.CreateTripRequestDTO;
import com.rota.facil.transport_service.http.dto.request.trip.JoinUserInTrip;
import com.rota.facil.transport_service.http.dto.request.tripUser.TripUserResponseDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.trip.TripResponseDTO;
import com.rota.facil.transport_service.messaging.producers.RabbitTransportTripEventProducer;
import com.rota.facil.transport_service.persistence.entities.*;
import com.rota.facil.transport_service.persistence.mappers.TripMapper;
import com.rota.facil.transport_service.persistence.mappers.TripUserMapper;
import com.rota.facil.transport_service.persistence.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {
    private final RabbitTransportTripEventProducer tripEventProducer;
    private final TripRepository tripRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final TripStatusRepository tripStatusRepository;
    private final TripUserRepository tripUserRepository;
    private final BoardPointRepository boardPointRepository;
    private final BoardPointRouteRepository boardPointRouteRepository;
    private final InstitutionRepository institutionRepository;
    private final TripMapper tripMapper;
    private final TripUserMapper tripUserMapper;
    private final UserRepository userRepository;


    @Transactional
    public TripResponseDTO register(CreateTripRequestDTO request) {
        BusEntity busFound = busRepository.findById(request.busId())
                .orElseThrow(BusNotFoundException::new);

        RouteEntity routeFound = routeRepository.findById(request.routeId())
                .orElseThrow(RouteNotFoundException::new);

        TripEntity preSaved = tripMapper.map(request);

        preSaved.setBus(busFound);
        preSaved.setRoute(routeFound);

        TripEntity saved = tripRepository.save(preSaved);

        tripEventProducer.createTripEvent(saved);

        saved.setTripStatus(new ArrayList<>());
        saved.getTripStatus().add(tripStatusRepository.save(TripStatusEntity.builder().trip(saved).build()));

        return tripMapper.map(saved);
    }

    public TripResponseDTO fetch(UUID tripId) {
        return tripMapper.map(this.fetchEntity(tripId));
    }

    public List<TripResponseDTO> list() {
        return tripRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(tripMapper::map)
                .toList();
    }


    @Transactional
    public TripUserResponseDTO join(UUID tripId, CurrentUser user, JoinUserInTrip request) {
        if (request.going() == null && request.return_() == null) throw new IllegalArgumentException();
        if (Boolean.FALSE.equals(request.going()) && Boolean.FALSE.equals(request.return_())) throw new IllegalArgumentException();
        if (tripUserRepository.existsByTripIdAndUserId(tripId, user.userId())) throw new UserAlreadyInTripException();


        TripEntity tripFound = this.fetchEntity(tripId);

        UserEntity userFound = userRepository.findById(user.userId())
                .orElseThrow(UserNotFoundException::new);

        BoardPointEntity boardPointFound = boardPointRouteRepository.findByIdAndTripId(request.boardPointId(), tripId)
                .orElseThrow(BoardPointNotFoundException::new);

        InstitutionEntity institutionFound = institutionRepository.findById(request.institutionId())
                .orElseThrow(InstitutionNotFoundException::new);

        TripUserEntity saved = tripUserRepository.save(
                TripUserEntity.builder()
                        .trip(tripFound)
                        .user(userFound)
                        .institution(institutionFound)
                        .boardPoint(boardPointFound)
                        .going(request.going() != null && request.going())
                        .return_(request.return_() != null && request.return_())
                        .build()
        );


        return tripUserMapper.map(saved);
    }

    private TripEntity fetchEntity(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(TripNotFoundException::new);
    }
}
