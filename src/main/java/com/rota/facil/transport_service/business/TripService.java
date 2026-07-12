package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.enums.Delay;
import com.rota.facil.transport_service.domain.enums.Presence;
import com.rota.facil.transport_service.domain.enums.Progress;
import com.rota.facil.transport_service.domain.enums.Role;
import com.rota.facil.transport_service.domain.exceptions.*;
import com.rota.facil.transport_service.http.dto.request.trip.CancelTripRequestDTO;
import com.rota.facil.transport_service.http.dto.request.trip.CreateTripRequestDTO;
import com.rota.facil.transport_service.http.dto.request.trip.JoinUserInTrip;
import com.rota.facil.transport_service.http.dto.response.tripUser.SimpleTripUserResponseDTO;
import com.rota.facil.transport_service.http.dto.response.tripUser.TripUserResponseDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.trip.TripResponseDTO;
import com.rota.facil.transport_service.messaging.producers.RabbitTransportTripEventProducer;
import com.rota.facil.transport_service.messaging.producers.RabbitTransportUserEventProducer;
import com.rota.facil.transport_service.persistence.dto.StudentPersistenceDTO;
import com.rota.facil.transport_service.persistence.entities.*;
import com.rota.facil.transport_service.persistence.mappers.TripMapper;
import com.rota.facil.transport_service.persistence.mappers.TripUserMapper;
import com.rota.facil.transport_service.persistence.repositories.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final BoardPointVisitedRepository boardPointVisitedRepository;
    private final RabbitTransportUserEventProducer transportUserEventProducer;
    private final TripMapper tripMapper;
    private final TripUserMapper tripUserMapper;
    private final UserRepository userRepository;

    @Transactional
    public TripResponseDTO register(CreateTripRequestDTO request, CurrentUser currentUser) {
        BusEntity busFound = busRepository.findByIdAndPrefectureId(request.busId(), currentUser.prefectureId())
                .orElseThrow(BusNotFoundException::new);

        RouteEntity routeFound = routeRepository.findById(request.routeId())
                .orElseThrow(RouteNotFoundException::new);

        if (busFound.getDriver() == null) throw new BusWithoutDriverException("Não é possível criar viagem com ônibus sem motorista vinculado");

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


    public Page<TripResponseDTO> list(CurrentUser currentUser, Pageable pageable) {
        return tripRepository.findAllByPrefectureIdToday(currentUser.prefectureId(), pageable)
                .map(tripMapper::map);
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

        tripFound.increaseStudents();
        TripEntity tripWithNewStudent = tripRepository.save(tripFound);

        TripUserEntity saved = tripUserRepository.save(
                TripUserEntity.builder()
                        .trip(tripWithNewStudent)
                        .user(userFound)
                        .institution(institutionFound)
                        .boardPoint(boardPointFound)
                        .going(userGoing)
                        .return_(userReturn)
                        .build()
        );

        List<UUID> userIds = List.of(userFound.getId());
        userRepository.increaseTripsByUserIds(userIds);
        transportUserEventProducer.increaseTripsUser(userIds);

        return tripUserMapper.map(saved);
    }


    @Transactional
    public TripResponseDTO init(UUID tripId, CurrentUser currentUser) {
        UserEntity driverFound = userRepository.findDriverById(currentUser.userId())
                .orElseThrow(UserNotFoundException::new);

        TripEntity tripFound = tripRepository.findByIdAndDriverId(tripId, driverFound.getId())
                .orElseThrow(TripNotFoundException::new);

        if (!Progress.NOT_STARTED.equals(tripFound.getActualStatus())) {
            throw new TripAlreadyStatedOrCancelledException("A ida só pode ser iniciada quando a viagem ainda não foi iniciada");
        }

        List<StudentPersistenceDTO> studentsInfo = tripUserRepository.findAllStudentsIdsAndEmailsByTripId(tripId);
        if (studentsInfo.isEmpty()) {
            throw new TripStatusAlreadyRegisteredException("Não é possível iniciar a ida sem alunos cadastrados na viagem");
        }

        Delay delay = this.getDelay(tripFound);

        TripStatusEntity newStatus = TripStatusEntity.builder()
                .trip(tripFound)
                .delay(delay)
                .progress(Progress.STARTED)
                .description(Progress.STARTED.getTitle())
                .build();

        tripFound.getTripStatus().add(newStatus);

        tripFound.setActualStatus(newStatus.getProgress());
        this.registerIgnoredInstitutionsForGoingTrip(tripFound);
        this.registerIgnoredBoardPointsForGoingTrip(tripFound);

        driverFound.moveToOnRoute();
        driverFound = userRepository.save(driverFound);

        List<UUID> driverIds = List.of(driverFound.getId());
        userRepository.increaseTripsByUserIds(driverIds);
        transportUserEventProducer.increaseTripsUser(driverIds);

        BusEntity bus = tripFound.getBus();
        bus.moveToOperation();
        bus = busRepository.save(bus);

        TripEntity saved = tripRepository.save(tripFound);
        tripEventProducer.runningTripEvent(saved, currentUser, studentsInfo);

        return tripMapper.map(saved);
    }

    @Transactional
    public TripResponseDTO initReturn(UUID tripId, CurrentUser currentUser) {
        UserEntity driverFound = userRepository.findDriverById(currentUser.userId())
                .orElseThrow(UserNotFoundException::new);

        TripEntity tripFound = tripRepository.findByIdAndDriverId(tripId, driverFound.getId())
                .orElseThrow(TripNotFoundException::new);

        if (!Progress.STARTED_FINISHED.equals(tripFound.getActualStatus())) {
            throw new TripStartedStillNotFinishYetException("A volta só pode ser iniciada quando a ida estiver finalizada");
        }

        TripStatusEntity newStatus = TripStatusEntity.builder()
                .trip(tripFound)
                .delay(this.getReturnStartDelay(tripFound))
                .progress(Progress.RETURN_STARTED)
                .description(Progress.RETURN_STARTED.getTitle())
                .build();

        tripFound.getTripStatus().add(newStatus);
        tripFound.setActualStatus(Progress.RETURN_STARTED);

        return tripMapper.map(tripRepository.save(tripFound));
    }

    @Transactional
    public TripResponseDTO cancel(UUID tripId, CurrentUser currentUser, CancelTripRequestDTO request) {
        UserEntity driverFound = userRepository.findDriverById(currentUser.userId())
                .orElseThrow(UserNotFoundException::new);

        TripEntity tripFound = tripRepository.findByIdAndDriverId(tripId, driverFound.getId())
                .orElseThrow(TripNotFoundException::new);

        if (tripStatusRepository.isTripCancelled(tripId)) throw new TripAlreadyCancelledException();

        TripStatusEntity newStatus =         TripStatusEntity.builder()
                .trip(tripFound)
                .delay(Delay.PUNCTUAL)
                .progress(Progress.CANCELLED)
                .build();

        tripFound.getTripStatus().add(newStatus);
        tripFound.setActualStatus(newStatus.getProgress());

        tripFound.setReasonOfCancellation(request.reasonOfCancellation());


        List<StudentPersistenceDTO> studentsInfo = tripUserRepository.findAllStudentsIdsAndEmailsByTripId(tripId);

        TripEntity saved = tripRepository.save(tripFound);

        driverFound.moveToAvailable();
        driverFound = userRepository.save(driverFound);

        BusEntity bus = tripFound.getBus();
        bus.moveToOutOfOperation();
        bus = busRepository.save(bus);

        tripEventProducer.cancelTripEvent(saved, currentUser, studentsInfo);
        return tripMapper.map(saved);
    }


    public List<TripResponseDTO> myTripsToday(CurrentUser user) {
        List<TripEntity> trips = new ArrayList<>();

        if (Role.DRIVER.equals(Role.valueOf(user.role()))) trips.addAll(tripRepository.findAllTodayByDriverId(user.userId()));
        if (Role.STUDENT.equals(Role.valueOf(user.role()))) trips.addAll(tripUserRepository.findAllTodayByPassengerId(user.userId()));

        return trips.stream()
                .map(tripMapper::map)
                .toList();
    }

    @Transactional
    public void processTrip(UUID tripId, double latitude, double longitude) {
        TripEntity tripFound = tripRepository.findByIdForUpdate(tripId)
                .orElseThrow(TripNotFoundException::new);

        tripFound.updateCoordinates(latitude, longitude);

        if (!tripStatusRepository.existsByTripIdAndProgress(tripId, Progress.STARTED)
                || tripStatusRepository.existsByTripIdAndProgress(tripId, Progress.CANCELLED)
                || tripStatusRepository.existsByTripIdAndProgress(tripId, Progress.RETURN_FINISHED)) {
            return;
        }

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

        boolean isGoing = !tripStatusRepository.existsByTripIdAndProgress(trip.getId(), Progress.STARTED_FINISHED)
                && this.inferGoingOrReturn(arrivalDate.toLocalTime(), routeFound.getGoing(), routeFound.getGoingFinish());
        boolean isReturn = tripStatusRepository.existsByTripIdAndProgress(trip.getId(), Progress.RETURN_STARTED)
                && this.inferGoingOrReturn(arrivalDate.toLocalTime(), routeFound.getReturn_(), routeFound.getReturnFinish());

        if (isGoing) {
            if (Boolean.TRUE.equals(newBoardPointVisitedFound.getGoing())
                    || trip.getIgnoredBoardPoints().contains(boardPoint)) return;

            newBoardPointVisitedFound.setGoing(true);
            boardPointVisitedRepository.save(newBoardPointVisitedFound);
            this.setStatusTrip(trip, Progress.BOARD_POINT_ARRIVAL, boardPoint.getName(), arrivalDate, routeFound);
            return;
        }

        if (isReturn) {
            if (Boolean.TRUE.equals(newBoardPointVisitedFound.getReturn_())
                    || trip.getIgnoredBoardPoints().contains(boardPoint)) return;

            newBoardPointVisitedFound.setReturn_(true);
            boardPointVisitedRepository.save(newBoardPointVisitedFound);

            this.setStatusTrip(trip, Progress.BOARD_POINT_ARRIVAL, boardPoint.getName(), arrivalDate, routeFound);

            if (tripStatusRepository.existsByTripIdAndProgress(trip.getId(), Progress.RETURN_FINISHED)) return;

            if (this.allInstitutionsAndBoardPointsWhereVisitedInReturn(routeFound, trip)) {
                this.setStatusTrip(trip, Progress.RETURN_FINISHED, arrivalDate, routeFound);
                this.setAbsences(trip, Progress.STARTED_FINISHED);
                UserEntity driverFound = trip.getBus().getDriver();

                driverFound.moveToAvailable();
                userRepository.save(driverFound);

                List<UUID> userIds = tripUserRepository.findAllUserIdsOfCompletedTripByTripId(trip.getId());

                userRepository.increaseTripCompletedByUserIds(userIds);
                BusEntity bus = trip.getBus();
                bus.moveToOutOfOperation();
                bus = busRepository.save(bus);

                this.transportUserEventProducer.completeTripUser(userIds);
            }
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

        boolean isGoing = !tripStatusRepository.existsByTripIdAndProgress(trip.getId(), Progress.STARTED_FINISHED)
                && this.inferGoingOrReturn(arrivalDate.toLocalTime(), routeFound.getGoing(), routeFound.getGoingFinish());
        boolean isReturn = tripStatusRepository.existsByTripIdAndProgress(trip.getId(), Progress.RETURN_STARTED)
                && this.inferGoingOrReturn(arrivalDate.toLocalTime(), routeFound.getReturn_(), routeFound.getReturnFinish());


        if (isGoing) {
            if (Boolean.TRUE.equals(newInstitutionVisitedFound.getGoing())
                    || trip.getIgnoredInstitutions().contains(institution)) return;

            newInstitutionVisitedFound.setGoing(true);
            newInstitutionVisitedFound = institutionVisitedRepository.save(newInstitutionVisitedFound);
            this.setStatusTrip(trip, Progress.INSTITUTION_ARRIVAL, institution.getName(), arrivalDate, routeFound);
            if (tripStatusRepository.existsByTripIdAndProgress(trip.getId(), Progress.STARTED_FINISHED)) return;

            if (this.allInstitutionsAndBoardPointsWhereVisitedInGoing(routeFound, trip)) {
                this.setStatusTrip(trip, Progress.STARTED_FINISHED, arrivalDate, routeFound);
                this.setAbsences(trip, Progress.STARTED_FINISHED);
                this.registerIgnoredInstitutionsForReturnTrip(trip);
                this.registerIgnoredBoardPointsForReturnTrip(trip);
            }
            return;
        }

        if (isReturn) {
            if (Boolean.TRUE.equals(newInstitutionVisitedFound.getReturn_())
                    || trip.getIgnoredInstitutions().contains(institution)) return;

            newInstitutionVisitedFound.setReturn_(true);
            newInstitutionVisitedFound = institutionVisitedRepository.save(newInstitutionVisitedFound);
            this.setStatusTrip(trip, Progress.INSTITUTION_ARRIVAL, institution.getName(), arrivalDate, routeFound);
        }

    }


    public List<SimpleTripUserResponseDTO> listStudents(UUID tripId, CurrentUser currentUser) {
        return tripUserRepository.findAllByPrefectureIdAndTripId(currentUser.prefectureId(), tripId)
                .stream()
                .map(tripUserMapper::mapToSimple)
                .toList();
    }


    public TripUserResponseDTO checkinTrip(UUID tripId, CurrentUser currentUser) {
        TripUserEntity tripUserFound = tripUserRepository.findNotFinishedByTripIdAndUserId(tripId, currentUser.userId())
                .orElseThrow(TripUserNotFoundException::new);
        tripUserFound.setPresence(Presence.CHECKIN);
        return tripUserMapper.map(tripUserRepository.save(tripUserFound));
    }

    public TripUserResponseDTO checkinTrip(UUID tripId, CurrentUser currentUser, Double latitude, Double longitude) {
//        TripUserEntity tripUserFound = tripUserRepository.findNotFinishedByTripIdAndUserIdAndCoordinates(tripId, currentUser.userId(), longitude, latitude)
//                .orElseThrow(TripUserNotFoundException::new);
//        tripUserFound.setPresent(true);
//        return tripUserMapper.map(tripUserRepository.save(tripUserFound));
        return null;
    }

    @Transactional
    public void exitTrip(UUID tripId, CurrentUser currentUser) {
        TripEntity tripFound = this.fetchEntity(tripId, currentUser.prefectureId());

        TripUserEntity tripUserFound = tripUserRepository.findNotStartedAndNotFinishedByTripIdAndUserId(tripId, currentUser.userId())
                .orElseThrow(TripUserNotFoundException::new);

        tripFound.decreaseStudents();
        tripRepository.save(tripFound);
        tripUserRepository.delete(tripUserFound);

        List<UUID> userIds = List.of(currentUser.userId());
        userRepository.decreaseTripsByUserIds(userIds);
        transportUserEventProducer.decreaseTripsUser(userIds);
    }

    private void setAbsences(TripEntity trip, Progress tripProgress) {
        tripUserRepository.setAbsentUsersOnTheTrip(trip.getId(), tripProgress);
    }

    private Delay getReturnStartDelay(TripEntity tripFound) {
        LocalTime expectedReturnStart = tripFound.getRoute().getReturn_();
        LocalTime actualReturnStart = LocalTime.now();

        if (actualReturnStart.equals(expectedReturnStart)) return Delay.PUNCTUAL;
        if (actualReturnStart.isBefore(expectedReturnStart)) return Delay.EARLY;
        return Delay.LATE;
    }

    private Delay getDelay(TripEntity tripFound) {
        RouteEntity route = tripFound.getRoute();
        LocalTime timeToStarted = route.getGoing();
        LocalTime timeToReturn = route.getGoingFinish();

        LocalTime realTimeStated = LocalTime.now();

        Delay delay;

        if (realTimeStated.isAfter(timeToStarted) && realTimeStated.isBefore(timeToReturn)) delay = Delay.LATE;
        else if (realTimeStated.isBefore(timeToStarted) && realTimeStated.isAfter(timeToStarted.minusMinutes(6L))) delay = Delay.EARLY;
        else if (realTimeStated.equals(timeToStarted)) delay = Delay.PUNCTUAL;
        else throw new InvalidTimeToInitTripException("Você só pode iniciar uma viagem com 6 minutos adiantados ou não é possível iniciar uma viagem quando o horário de volta já deveria ser iniciado\nTempo do evento ocorrido: " + realTimeStated + " inicio: " + timeToStarted + " fim: " + timeToReturn);
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
    protected void registerIgnoredBoardPointsForReturnTrip(TripEntity tripFound) {
        this.registerIgnoredBoardPointsForTrip(tripFound, false);
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
        return this.allInstitutionsWhereVisited(route, trip, false, true) && this.allBoardPointsWhereVisited(route, trip, false, true);
    }

    private boolean allInstitutionsWhereVisited(RouteEntity route, TripEntity trip, boolean going, boolean return_) {
        List<InstitutionVisitedEntity> institutionsVisited  = institutionVisitedRepository.findByTripIdAndGoingAndReturn(trip.getId(), going, return_);
        Set<InstitutionEntity> institutionsToBeVisited = this.fetchInstitutionsToBeVisited(route, trip);
        Set<InstitutionEntity> visitedRequiredInstitutions = institutionsVisited.stream()
                .map(InstitutionVisitedEntity::getInstitution)
                .filter(institutionsToBeVisited::contains)
                .collect(java.util.stream.Collectors.toSet());
        return visitedRequiredInstitutions.containsAll(institutionsToBeVisited);
    }

    private boolean allBoardPointsWhereVisited(RouteEntity route, TripEntity trip, boolean going, boolean return_) {
        Set<BoardPointEntity> boardPointsToBeVisited = this.fetchBoardPointToBeVisited(route, trip);
        List<BoardPointVisitedEntity> boardPointsVisited = boardPointVisitedRepository.findReturnByTripId(trip.getId(), going, return_);
        Set<BoardPointEntity> visitedRequiredBoardPoints = boardPointsVisited.stream()
                .map(BoardPointVisitedEntity::getBoardPoint)
                .filter(boardPointsToBeVisited::contains)
                .collect(java.util.stream.Collectors.toSet());
        return visitedRequiredBoardPoints.containsAll(boardPointsToBeVisited);
    }

    private void setStatusTrip(TripEntity trip, Progress progress, LocalDateTime arrivalDate, RouteEntity route) {
        this.saveTripStatus(trip, progress, null, arrivalDate, route);
    }

    private void setStatusTrip(TripEntity trip, Progress progress, String placeName, LocalDateTime arrivalDate, RouteEntity route) {
        this.saveTripStatus(trip, progress, placeName, arrivalDate, route);
    }

    private void saveTripStatus(TripEntity trip, Progress progress, String placeName, LocalDateTime arrivalDate, RouteEntity route) {
        this.validateProgressTripToSave(trip, progress);

        TripStatusEntity newStatus = TripStatusEntity.builder()
                .trip(trip)
                .progress(progress)
                .delay(route.calculateDelay(arrivalDate.toLocalTime(), progress))
                .description(placeName != null ? progress.getTitle() + placeName : progress.getTitle())
                .build();

        trip.getTripStatus().add(newStatus);
        trip.setActualStatus(newStatus.getProgress());

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
        LocalDate now = LocalDate.now();
        LocalDateTime startWithTol = LocalDateTime.of(now, startInterval);
        LocalDateTime endWithTol = LocalDateTime.of(now, finishInterval);
        LocalDateTime arrivalDateTime = LocalDateTime.of(now, arrivalDate);
        return !arrivalDateTime.isBefore(startWithTol.minusMinutes(6L)) && !arrivalDateTime.isAfter(endWithTol.plusMinutes(6L));
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
