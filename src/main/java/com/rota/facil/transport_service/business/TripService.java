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
import java.time.temporal.TemporalUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TripService {

    @Value("${init.trip.before.minutes}")
    private Long INIT_TRIP_BEFORE_MINUTES;

    @Value("${init.trip.after.minutes}")
    private Long INIT_TRIP_AFTER_MINUTES;

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
    private final BoardPointVisitedRepository boardPointVisitedRepository;
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

    public TripResponseDTO fetch(UUID tripId, CurrentUser currentUser) {
        return tripMapper.map(this.fetchEntity(tripId, currentUser.prefectureId()));
    }


    public List<TripResponseDTO> list(CurrentUser currentUser) {
        return tripRepository.findAllByPrefectureId(currentUser.prefectureId())
                .stream()
                .map(tripMapper::map)
                .toList();
    }


    @Transactional
    public TripUserResponseDTO join(UUID tripId, CurrentUser user, JoinUserInTrip request) {
        if (request.going() == null && request.return_() == null) throw new IllegalArgumentException();
        if (Boolean.FALSE.equals(request.going()) && Boolean.FALSE.equals(request.return_())) throw new IllegalArgumentException();
        if (tripUserRepository.existsByTripIdAndUserId(tripId, user.userId())) throw new UserAlreadyInTripException();


        boolean userGoing = request.going() != null && request.going();
        boolean userReturn = request.return_() != null && request.return_();

        TripEntity tripFound = tripRepository.findNotStartedByIdAndPrefectureId(tripId, user.prefectureId())
                .orElseThrow(TripNotFoundException::new);


        BusEntity bus = tripFound.getBus();

        int passengers = tripUserRepository.countPassengersByTripIdAndGoingAndReturn(tripId, userGoing, userReturn) + 1;

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
                        .going(userGoing)
                        .return_(userReturn)
                        .build()
        );


        return tripUserMapper.map(saved);
    }


    @Transactional
    public TripResponseDTO init(UUID tripId, CurrentUser currentUser) {
        UserEntity driverFound = userRepository.findDriverById(currentUser.userId())
                .orElseThrow(UserNotFoundException::new);

        TripEntity tripFound = tripRepository.findByIdAndDriverId(tripId, driverFound.getId())
                .orElseThrow(TripNotFoundException::new);

        if (tripStatusRepository.isTripInitOrCancelled(tripFound.getId())) throw new TripAlreadyStatedOrCancelledException();

        Delay delay = this.getDelay(tripFound);

        tripFound.getTripStatus().add(
                TripStatusEntity.builder()
                        .trip(tripFound)
                        .delay(delay)
                        .progress(Progress.STARTED)
                        .build()
        );

        this.registerIgnoredInstitutionsForGoingTrip(tripFound);
        this.registerIgnoredBoardPointsForGoingTrip(tripFound);

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
    public void processTrip(UUID tripId, double latitude, double longitude) {
        TripEntity tripFound = tripRepository.findById(tripId)
                .orElseThrow(TripNotFoundException::new);

        tripFound.updateCoordinates(latitude, longitude);

        Optional<InstitutionEntity> institutionExisting = routeRepository.findInstitutionByTripIdAndCoordinates(tripId, longitude, latitude);
        Optional<BoardPointEntity> boardPointExisting = routeRepository.findBoardPointByTripIdAndCoordinates(tripId, longitude, latitude);

        LocalDateTime now = LocalDateTime.now();
        institutionExisting.ifPresent(institution -> this.inferInstitutionArrival(institution, tripFound, now));
        boardPointExisting.ifPresent(boardPoint -> this.inferBoardPointArrival(boardPoint, tripFound, now));
    }

    @Transactional
    public void inferBoardPointArrival(BoardPointEntity boardPoint, TripEntity trip, LocalDateTime arrivalDate) {
        RouteEntity routeFound = routeRepository.findByTripId(trip.getId())
                .orElseThrow(RouteNotFoundException::new);

        BoardPointVisitedEntity newBoardPointVisitedFound = boardPointVisitedRepository.findByBoardPointIdAndTripId(boardPoint.getId(), trip.getId())
                .orElseGet(() -> BoardPointVisitedEntity.builder()
                        .boardPoint(boardPoint)
                        .trip(trip)
                        .build()
                );

        boolean isGoing = this.inferGoingOrReturn(arrivalDate.toLocalTime(), routeFound.getGoing(), routeFound.getGoingFinish());
        boolean isReturn = this.inferGoingOrReturn(arrivalDate.toLocalTime(), routeFound.getReturn_(), routeFound.getReturnFinish());

        newBoardPointVisitedFound.setGoing(isGoing);
        newBoardPointVisitedFound.setReturn_(isReturn);

        if (isGoing) {
            this.setStatusTrip(trip, Progress.BOARD_POINT_ARRIVAL, boardPoint.getName(), arrivalDate, routeFound);
            return;
        }

        if (isReturn) {
            this.setStatusTrip(trip, Progress.BOARD_POINT_ARRIVAL, boardPoint.getName(), arrivalDate, routeFound);

            if (tripStatusRepository.existsByTripIdAndProgress(trip.getId(), Progress.RETURN_FINISHED)) return;
            if (this.allInstitutionsAndBoardPointsWhereVisitedInReturn(routeFound, trip)) this.setStatusTrip(trip, Progress.RETURN_FINISHED, arrivalDate, routeFound);
        }

    }

    @Transactional
    public void inferInstitutionArrival(InstitutionEntity institution, TripEntity trip, LocalDateTime arrivalDate) {
        RouteEntity routeFound = routeRepository.findByTripId(trip.getId())
                .orElseThrow(RouteNotFoundException::new);

        InstitutionVisitedEntity newInstitutionVisitedFound = institutionVisitedRepository.findByInstitutionIdAndTripId(institution.getId(), trip.getId())
                .orElseGet( () -> InstitutionVisitedEntity.builder()
                            .institution(institution)
                            .trip(trip)
                            .build()
                );

        boolean isGoing = this.inferGoingOrReturn(arrivalDate.toLocalTime(), routeFound.getGoing(), routeFound.getGoingFinish());
        boolean isReturn = this.inferGoingOrReturn(arrivalDate.toLocalTime(), routeFound.getReturn_(), routeFound.getGoingFinish());

        newInstitutionVisitedFound.setGoing(isGoing);
        newInstitutionVisitedFound.setReturn_(isReturn);

        if (isGoing) {
            this.setStatusTrip(trip, Progress.INSTITUTION_ARRIVAL, institution.getName(), arrivalDate, routeFound);
            if (tripStatusRepository.existsByTripIdAndProgress(trip.getId(), Progress.STARTED_FINISHED)) return;

            if (this.allInstitutionsAndBoardPointsWhereVisitedInGoing(routeFound, trip)) {
                this.setStatusTrip(trip, Progress.STARTED_FINISHED, arrivalDate, routeFound);
                this.registerIgnoredInstitutionsForReturnTrip(trip);
            }
        }

        if (isReturn) {
            this.setStatusTrip(trip, Progress.INSTITUTION_ARRIVAL, institution.getName(), arrivalDate, routeFound);
            if (tripStatusRepository.existsByTripIdAndProgress(trip.getId(), Progress.RETURN_STARTED)) return;

            this.setStatusTrip(trip, Progress.RETURN_STARTED, arrivalDate, routeFound);
        }

    }

    private Delay getDelay(TripEntity tripFound) {
        RouteEntity route = tripFound.getRoute();
        LocalTime timeToStarted = route.getGoing();
        LocalTime timeToReturn = route.getReturn_();

        LocalTime realTimeStated = LocalTime.now();

        Delay delay;

        if (realTimeStated.isAfter(timeToStarted) && realTimeStated.isBefore(timeToReturn)) delay = Delay.LATE;
        else if (realTimeStated.isBefore(timeToStarted) && realTimeStated.isAfter(timeToStarted.minusMinutes(6L))) delay = Delay.EARLY;
        else if (realTimeStated.equals(timeToStarted)) delay = Delay.PUNCTUAL;
        else throw new InvalidTimeToInitTrip("Você só pode iniciar uma viagem com 6 minutos adiantados ou não é possível iniciar uma viagem quando o horário de volta já deveria ser iniciado");
        return delay;
    }


    @Transactional
    protected void registerIgnoredInstitutionsForGoingTrip(TripEntity tripFound) {
        this.registerIgnoredInstitutionsForTrip(tripFound, true);
    }

    @Transactional
    protected void registerIgnoredInstitutionsForReturnTrip(TripEntity tripFound) {
        this.registerIgnoredInstitutionsForTrip(tripFound, false);
    }

    @Transactional
    protected void registerIgnoredInstitutionsForTrip(TripEntity tripFound, boolean isGoing) {
        List<InstitutionEntity> allInstitutionsToBeVisitedInRoute = new ArrayList<>(institutionRepository.findAllByTripId(tripFound.getId()));
        List<InstitutionEntity> allInstitutionsShouldActuallyBeVisited;

        if (isGoing) {
            allInstitutionsShouldActuallyBeVisited = tripUserRepository.findAllInstitutionsGoingByTripId(tripFound.getId());
        }
        else {
            tripFound.getIgnoredInstitutions().clear();
            allInstitutionsShouldActuallyBeVisited = tripUserRepository.findAllInstitutionsReturnByTripId(tripFound.getId());
        }

        allInstitutionsToBeVisitedInRoute.removeAll(allInstitutionsShouldActuallyBeVisited);

        List<InstitutionEntity> ignoredInstitutions = List.copyOf(allInstitutionsToBeVisitedInRoute);

        tripFound.setIgnoredInstitutions(new HashSet<>());

        for (InstitutionEntity ignoredInstitution : ignoredInstitutions) tripFound.getIgnoredInstitutions().add(ignoredInstitution);
        tripRepository.save(tripFound);
    }

    @Transactional
    protected void registerIgnoredBoardPointsForGoingTrip(TripEntity tripFound) {
        this.registerIgnoredBoardPointsForTrip(tripFound, true);
    }

    @Transactional
    protected void registerIgnoredBoardPointsForTrip(TripEntity tripFound, boolean isGoing) {
        List<BoardPointEntity> allBoardPointsToBeVisitedInRoute = new ArrayList<>(boardPointRepository.findAllByTripId(tripFound.getId()));
        List<BoardPointEntity> allBoardPointsShouldActuallyBeVisited;

        if (isGoing) {
            allBoardPointsShouldActuallyBeVisited = tripUserRepository.findAllBoardPointsGoingByTripId(tripFound.getId());
        }
        else {
            tripFound.getIgnoredBoardPoints().clear();
            allBoardPointsShouldActuallyBeVisited = tripUserRepository.findAllBoardPointsReturnByTripId(tripFound.getId());
        }

        allBoardPointsToBeVisitedInRoute.removeAll(allBoardPointsShouldActuallyBeVisited);

        List<BoardPointEntity> ignoredBoardPoints = List.copyOf(allBoardPointsToBeVisitedInRoute);

        tripFound.setIgnoredBoardPoints(new HashSet<>());

        for (BoardPointEntity ignoredBoardPoint : ignoredBoardPoints) tripFound.getIgnoredBoardPoints().add(ignoredBoardPoint);
        tripRepository.save(tripFound);
    }


    private boolean allInstitutionsAndBoardPointsWhereVisitedInGoing(RouteEntity route, TripEntity trip) {
        return this.allInstitutionsWhereVisited(route, trip, true, false) && this.allBoardPointsWhereVisited(route, trip, true, false);
    }

    private boolean allInstitutionsAndBoardPointsWhereVisitedInReturn(RouteEntity route, TripEntity trip) {
        return this.allInstitutionsWhereVisited(route, trip, true, true) && this.allBoardPointsWhereVisited(route, trip, true, true);
    }

    private boolean allInstitutionsWhereVisited(RouteEntity route, TripEntity trip, boolean going, boolean return_) {
        List<InstitutionVisitedEntity> institutionsVisited  = institutionVisitedRepository.findByTripIdAndGoingAndReturn(trip.getId(), going, return_);
        Set<InstitutionEntity> institutionsToBeVisited = this.fetchInstitutionsToBeVisited(route, trip);
        return (institutionsToBeVisited.size() == institutionsVisited.size());
    }

    private boolean allBoardPointsWhereVisited(RouteEntity route, TripEntity trip, boolean going, boolean return_) {
        Set<BoardPointEntity> boardPointsToBeVisited = this.fetchBoardPointToBeVisited(route, trip);
        List<BoardPointVisitedEntity> boardPointsVisited = boardPointVisitedRepository.findReturnByTripId(trip.getId(), going, return_);
        return (boardPointsToBeVisited.size() == boardPointsVisited.size());
    }

    private void setStatusTrip(TripEntity trip, Progress progress, LocalDateTime arrivalDate, RouteEntity route) {
        this.saveTripStatus(trip, progress, null, arrivalDate, route);
    }

    private void setStatusTrip(TripEntity trip, Progress progress, String placeName, LocalDateTime arrivalDate, RouteEntity route) {
        this.saveTripStatus(trip, progress, placeName, arrivalDate, route);
    }

    private void saveTripStatus(TripEntity trip, Progress progress, String placeName, LocalDateTime arrivalDate, RouteEntity route) {
        this.validateProgressTripToSave(trip, progress);
        trip.getTripStatus().add(
                TripStatusEntity.builder()
                        .trip(trip)
                        .progress(progress)
                        .delay(route.calculateDelay(arrivalDate.toLocalTime(), progress))
                        .description(placeName != null ? progress.getTitle() + placeName : progress.getTitle())
                        .build()
        );

        tripRepository.save(trip);
    }

    private void validateProgressTripToSave(TripEntity trip, Progress progress) {
        if (progress.equals(Progress.RETURN_STARTED) && !tripStatusRepository.existsByTripIdAndProgress(trip.getId(), Progress.STARTED_FINISHED)) throw new TripStartedStillNotFinishYetException();
    }


    private Set<InstitutionEntity> fetchInstitutionsToBeVisited(RouteEntity routeFound, TripEntity trip) {
        Set<InstitutionEntity> institutions = new HashSet<>(routeFound.getInstitutions());
        institutions.removeAll(trip.getIgnoredInstitutions());
        return institutions;
    }

    private Set<BoardPointEntity> fetchBoardPointToBeVisited(RouteEntity routeFound, TripEntity trip) {
        Set<BoardPointEntity> boardPoints = new HashSet<>(routeFound.getBoardPoints().stream().map(BoardPointRouteEntity::getBoardPoint).toList());
        boardPoints.removeAll(trip.getIgnoredBoardPoints());
        return boardPoints;
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

    private TripEntity fetchEntity(UUID tripId, UUID prefectureId) {
        return tripRepository.findByIdAndPrefectureId(tripId, prefectureId)
                .orElseThrow(TripNotFoundException::new);
    }
}
