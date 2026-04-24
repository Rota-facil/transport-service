package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.enums.Delay;
import com.rota.facil.transport_service.domain.enums.Progress;
import com.rota.facil.transport_service.domain.enums.Role;
import com.rota.facil.transport_service.domain.exceptions.*;
import com.rota.facil.transport_service.http.dto.request.trip.CancelTripRequestDTO;
import com.rota.facil.transport_service.http.dto.request.trip.CreateTripRequestDTO;
import com.rota.facil.transport_service.http.dto.request.trip.JoinUserInTrip;
import com.rota.facil.transport_service.http.dto.response.tripUser.TripUserResponseDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.trip.TripResponseDTO;
import com.rota.facil.transport_service.messaging.producers.RabbitTransportTripEventProducer;
import com.rota.facil.transport_service.persistence.entities.*;
import com.rota.facil.transport_service.persistence.mappers.TripMapper;
import com.rota.facil.transport_service.persistence.mappers.TripUserMapper;
import com.rota.facil.transport_service.persistence.repositories.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TripService {
    private static final Logger log = LoggerFactory.getLogger(TripService.class);
    private final RabbitTransportTripEventProducer tripEventProducer;
    private final TripRepository tripRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final TripStatusRepository tripStatusRepository;
    private final TripUserRepository tripUserRepository;
    private final BoardPointRepository boardPointRepository;
    private final BoardPointRouteRepository boardPointRouteRepository;
    private final InstitutionRepository institutionRepository;
    private final InstitutionVisitedRepository institutionVisitedRepository;
    private final TripMapper tripMapper;
    private final TripUserMapper tripUserMapper;
    private final UserRepository userRepository;

    @Value("${init.trip.before.minutes}")
    private Long INIT_TRIP_BEFORE_MINUTES;

    @Value("${init.trip.after.minutes}")
    private Long INIT_TRIP_AFTER_MINUTES;

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

        BusEntity bus = tripFound.getBus();

        int passengers = tripUserRepository.countPassengersByTripId(tripId) + 1;

        if (passengers > bus.getCapacity()) throw new MaxBusCapacityException("Não é possível se inscrever nessa viagem porque a lista de passageiros já está lotada");

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

    public TripResponseDTO init(UUID tripId, CurrentUser currentUser) {
        UserEntity driverFound = userRepository.findDriverById(currentUser.userId())
                .orElseThrow(UserNotFoundException::new);

        TripEntity tripFound = tripRepository.findByIdAndDriverId(tripId, driverFound.getId())
                .orElseThrow(TripNotFoundException::new);

        if (tripStatusRepository.isTripInitOrCancelled(tripFound.getId())) throw new TripAlreadyStatedOrCancelledException();

        RouteEntity route = tripFound.getRoute();

        LocalTime timeToStarted = route.getGoing();
        LocalTime realTimeStated = LocalTime.now();

        Delay delay;

        if (realTimeStated.isAfter(timeToStarted) && ChronoUnit.MINUTES.between(timeToStarted, realTimeStated) <= INIT_TRIP_AFTER_MINUTES) delay = Delay.LATE;
        else if (realTimeStated.isBefore(timeToStarted) && ChronoUnit.MINUTES.between(realTimeStated, timeToStarted) <= INIT_TRIP_BEFORE_MINUTES) delay = Delay.EARLY;
        else if (realTimeStated.equals(timeToStarted)) delay = Delay.PUNCTUAL;
        else throw new InvalidTimeToInitTrip("Você só pode iniciar uma viagem com " + INIT_TRIP_BEFORE_MINUTES + " minutos adiantados ou " + INIT_TRIP_AFTER_MINUTES + " minutos de atraso");

        tripFound.getTripStatus().add(
                TripStatusEntity.builder()
                        .trip(tripFound)
                        .delay(delay)
                        .progress(Progress.STARTED)
                        .build()
        );

        return tripMapper.map(tripRepository.save(tripFound));
    }


    @Transactional
    public TripResponseDTO cancel(UUID tripId, CurrentUser currentUser, CancelTripRequestDTO request) {
        UserEntity driverFound = userRepository.findDriverById(currentUser.userId())
                .orElseThrow(UserNotFoundException::new);

        TripEntity tripFound = tripRepository.findByIdAndDriverId(tripId, driverFound.getId())
                .orElseThrow(TripNotFoundException::new);

        if (tripStatusRepository.isTripCancelled(tripId)) throw new TripAlreadyCancelledException();

        tripFound.getTripStatus().add(
                TripStatusEntity.builder()
                        .trip(tripFound)
                        .delay(Delay.PUNCTUAL)
                        .progress(Progress.CANCELLED)
                        .build()
        );

        tripFound.setReasonOfCancellation(request.reasonOfCancellation());


        List<String> emails = tripUserRepository.findAllEmailsByTripId(tripId);

        TripEntity saved = tripRepository.save(tripFound);

        if (!emails.isEmpty()) tripEventProducer.cancelTripEvent(saved, currentUser, emails);
        return tripMapper.map(saved);
    }


    public List<TripUserResponseDTO> myTrips(CurrentUser user) {
        List<TripUserEntity> trips = new ArrayList<>();

        if (Role.DRIVER.equals(Role.valueOf(user.role()))) trips.addAll(tripUserRepository.findAllByDriverId(user.userId()));
        if (Role.STUDENT.equals(Role.valueOf(user.role()))) trips.addAll(tripUserRepository.findAllByPassengerId(user.userId()));

        return trips.stream()
                .map(tripUserMapper::map)
                .toList();
    }

    @Transactional
    public void inferInstitutionArrival(UUID institutionId, UUID tripId, LocalDateTime arrivalDate) {
        RouteEntity routeFound = routeRepository.findByTripId(tripId)
                .orElseThrow(RouteNotFoundException::new);
        TripEntity tripFound = tripRepository.findById(tripId)
                .orElseThrow(TripNotFoundException::new);

        InstitutionVisitedEntity newInstitutionVisitedFound = institutionVisitedRepository.findByInstitutionIdAndTripId(institutionId, tripId)
                .orElseGet( () -> {
                    InstitutionEntity institutionFound = institutionRepository.findById(institutionId)
                                    .orElseThrow(InstitutionNotFoundException::new);

                    return InstitutionVisitedEntity.builder()
                            .institution(institutionFound)
                            .trip(tripFound)
                            .build();
                });

        boolean isGoing = this.inferGoingOrReturn(arrivalDate.toLocalTime(), routeFound.getGoing(), routeFound.getGoingFinish());
        boolean isReturn = this.inferGoingOrReturn(arrivalDate.toLocalTime(), routeFound.getReturn_(), routeFound.getGoingFinish());
        Progress progress = null;

        Set<InstitutionEntity> institutionsToBeVisited = this.fetchInstitutionsToBeVisited(routeFound, tripFound);
        List<InstitutionVisitedEntity> institutionsVisited = new ArrayList<>();

        if (isGoing) {
            progress = Progress.STARTED_FINISHED;
            newInstitutionVisitedFound.setGoing(true);
            institutionsVisited  = institutionVisitedRepository.findGoingByTripId(tripId);
        }

        if (isReturn) {
            progress = Progress.RETURN_STARTED;
            newInstitutionVisitedFound.setReturn_(true);
            institutionsVisited  = institutionVisitedRepository.findReturnByTripId(tripId);
        }

        if (progress == null) {
            log.warn("Evento de chegada fora de qualquer intervalo de tempo configurado para a trip {}", tripId);
            return;
        }

        if (tripStatusRepository.existsByTripIdAndProgress(tripId, progress)) return;

        institutionsVisited.add(institutionVisitedRepository.save(newInstitutionVisitedFound));

        boolean allInstitutionsWhereVisited = (institutionsToBeVisited.size() == institutionsVisited.size());

        if (allInstitutionsWhereVisited) this.setStatusTrip(tripFound, progress, arrivalDate, routeFound);
    }

    private void setStatusTrip(TripEntity trip, Progress progress, LocalDateTime arrivalDate, RouteEntity route) {
        if (progress.equals(Progress.RETURN_STARTED) && !tripStatusRepository.existsByTripIdAndProgress(trip.getId(), Progress.STARTED_FINISHED)) throw new TripStartedStillNotFinishYetException();
        trip.getTripStatus().add(
                TripStatusEntity.builder()
                        .trip(trip)
                        .progress(progress)
                        .delay(route.calculateDelay(arrivalDate.toLocalTime(), progress))
                        .build()
        );

        tripRepository.save(trip);
    }

    private Set<InstitutionEntity> fetchInstitutionsToBeVisited(RouteEntity routeFound, TripEntity trip) {
        Set<InstitutionEntity> institutions = new HashSet<>(routeFound.getInstitutions());
        institutions.removeAll(trip.getIgnoredInstitutions());
        return institutions;
    }

    private boolean inferGoingOrReturn(LocalTime arrivalDate, LocalTime startInterval, LocalTime finishInterval) {
        LocalTime startWithTol = startInterval.minusMinutes(6L);
        LocalTime endWithTol = finishInterval.plusMinutes(6L);
        return !arrivalDate.isBefore(startWithTol) && !arrivalDate.isAfter(endWithTol);
    }

    private TripEntity fetchEntity(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(TripNotFoundException::new);
    }
}
