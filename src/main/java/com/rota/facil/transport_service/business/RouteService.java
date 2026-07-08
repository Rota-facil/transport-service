package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.enums.DaysOfWeek;
import com.rota.facil.transport_service.domain.exceptions.*;
import com.rota.facil.transport_service.http.client.IntelligenceHttpClient;
import com.rota.facil.transport_service.http.client.mappers.IntelligenceMapper;
import com.rota.facil.transport_service.http.dto.request.route.*;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.client.intelligence.RouteInterpretationResponseDTO;
import com.rota.facil.transport_service.http.dto.response.route.RouteHeatMapResponseDTO;
import com.rota.facil.transport_service.http.dto.response.route.RouteResponseDTO;
import com.rota.facil.transport_service.persistence.entities.*;
import com.rota.facil.transport_service.persistence.mappers.RouteMapper;
import com.rota.facil.transport_service.persistence.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
    private final InstitutionRepository institutionRepository;
    private final BoardPointRepository boardPointRepository;
    private final BusRepository busRepository;
    private final TripRepository tripRepository;
    private final TripStatusRepository tripStatusRepository;
    private final IntelligenceHttpClient intelligenceHttpClient;
    private final RouteMapper routeMapper;
    private final IntelligenceMapper intelligenceMapper;
    private final TripUserRepository tripUserRepository;
    private final RouteRecurringRepository routeRecurringRepository;

    @Transactional
    public RouteResponseDTO register(CreateRouteRequestDTO request, CurrentUser currentUser) {
        if (request.busIds().isEmpty()) {
            throw new BusNotFoundException("Selecione pelo menos um ônibus para a recorrência da rota");
        }

        Set<InstitutionEntity> institutionsFound = institutionRepository.findAllSetById(request.institutionsIds());
        List<BusEntity> busListFound = busRepository.findAllActiveByIdInAndPrefectureId(request.busIds(), currentUser.prefectureId());

        if (institutionsFound.size() != request.institutionsIds().size()) throw new InstitutionNotFoundException("Erro ao encontrar instituições selecionadas. Selecione apenas instituições existentes");
        if (busListFound.size() != request.busIds().size()) throw new BusNotFoundException("Erro ao encontrar ônibus selecionado. Selecione apenas ônibus existentes");
        if (busListFound.stream().anyMatch(bus -> bus.getDriver() == null)) throw new BusWithoutDriverException("Não é possível criar rota com ônibus sem motorista vinculado");

        RouteEntity preSaved = routeMapper.map(request);
        preSaved.setInstitutions(institutionsFound);
        preSaved.setPrefectureId(currentUser.prefectureId());

        List<RouteRecurringEntity> recurringEntity = new ArrayList<>();

        for (BusEntity bus : busListFound) {
            recurringEntity.add(
                    RouteRecurringEntity.builder()
                            .route(preSaved)
                            .bus(bus)
                            .build()
            );
        }

        preSaved.setRecurring(recurringEntity);
        RouteEntity saved = routeRepository.save(preSaved);

        List<UUID> boardPointsIds = request.boardPoints().stream().map(CreateBoardPointRouteRequestDTO::boardPointId).toList();

        List<BoardPointEntity> boardPointsFound = boardPointRepository.findAllActiveByIdIn(boardPointsIds);

        Map<UUID, CreateBoardPointRouteRequestDTO> boardPointsRequestMap = request.boardPoints().stream().collect(Collectors.toMap(
                CreateBoardPointRouteRequestDTO::boardPointId,
                boardPoint -> boardPoint
        ));

        Map<UUID, BoardPointEntity> boardPointsFoundMap = boardPointsFound.stream()
                .collect(Collectors.toMap(
                        com.rota.facil.transport_service.persistence.entities.BoardPointEntity::getId,
                        boardPoint -> boardPoint
                ));

        if (boardPointsIds.size() != boardPointsFoundMap.size()) throw new BoardPointNotFoundException("Erro ao encontrar pontos de embarque selecionadas. Selecione apenas ponstos de embarque existentes");

        List<BoardPointRouteEntity> boardPointRoutes = new ArrayList<>();

        for (UUID boardPointId : boardPointsIds) {
            BoardPointEntity boardPointFoundMap = boardPointsFoundMap.get(boardPointId);
            CreateBoardPointRouteRequestDTO boardPointFoundRequest = boardPointsRequestMap.get(boardPointId);
            boardPointRoutes.add(
                    BoardPointRouteEntity.builder()
                            .route(saved)
                            .boardPoint(boardPointFoundMap)
                            .boardTimeGoing(boardPointFoundRequest.boardTimeGoing())
                            .boardTimeFinish(boardPointFoundRequest.boardTimeFinish())
                            .build()
            );
        }

        if (saved.getBoardPoints() == null) saved.setBoardPoints(new ArrayList<>());

        saved.getBoardPoints().addAll(boardPointRoutes);

        RouteEntity newSaved = routeRepository.save(saved);

        if (request.daysOfWeek().contains(DaysOfWeek.getFromValueDay(LocalDate.now().getDayOfWeek().getValue()))) {

            List<TripEntity> tripEntities = new ArrayList<>();

            for (BusEntity bus : busListFound) {
                TripEntity createdTrip = TripEntity.builder()
                        .name(newSaved.getName())
                        .route(newSaved)
                        .bus(bus)
                        .prefectureId(currentUser.prefectureId())
                        .build();
                TripStatusEntity status = TripStatusEntity.builder().trip(createdTrip).build();

                createdTrip.setTripStatus(new ArrayList<>());
                createdTrip.getTripStatus().add(status);
                createdTrip.setActualStatus(status.getProgress());

                tripEntities.add(createdTrip);
            }

            tripRepository.saveAll(tripEntities);
        }

        return routeMapper.map(newSaved);
    }

    public RouteResponseDTO fetch(UUID routeId, CurrentUser currentUser) {
        return routeMapper.map(this.fetchEntity(routeId, currentUser.prefectureId()));
    }

    public Page<RouteResponseDTO> list(CurrentUser currentUser, Pageable pageable) {
        return routeRepository.findAllByPrefectureId(currentUser.prefectureId(), pageable)
                .map(routeMapper::map);
    }

    public List<RouteResponseDTO> listSimple(CurrentUser currentUser) {
        return routeRepository.findAllByPrefectureId(currentUser.prefectureId())
                .stream()
                .map(routeMapper::map)
                .toList();
    }

    public RouteInterpretationResponseDTO interpreterRoute(UUID routeId) {
        RouteEntity routeFoud = this.fetchEntity(routeId);
        List<TripEntity> tripsFound = tripRepository.findAllFinishedByRouteId(routeId);

        if (tripsFound.isEmpty()) throw new TripNotFoundException("Esssa rota ainda não tem viagens finalizadas para poder gerar interpretação");

        RouteInterpretationResponseDTO interpretationResponse = intelligenceHttpClient.generateRouteInterpretation(intelligenceMapper.map(routeFoud, tripsFound));

        routeFoud.setInterpretation(interpretationResponse.routeInterpretation());
        routeRepository.save(routeFoud);
        return interpretationResponse;
    }

    @Transactional
    public RouteEntity addBoardPoints(UUID routeId, List<CreateBoardPointRouteRequestDTO> request, CurrentUser currentUser) {
        RouteEntity route = routeRepository.findByIdAndPrefectureId(routeId, currentUser.prefectureId())
                .orElseThrow(RouteNotFoundException::new);
        List<UUID> boardPointsIds = request.stream().map(CreateBoardPointRouteRequestDTO::boardPointId).toList();

        List<BoardPointEntity> boardPointsFound = boardPointRepository.findAllActiveByIdIn(boardPointsIds);

        Map<UUID, CreateBoardPointRouteRequestDTO> boardPointsRequestMap = request.stream().collect(Collectors.toMap(
                CreateBoardPointRouteRequestDTO::boardPointId,
                boardPoint -> boardPoint
        ));

        Map<UUID, BoardPointEntity> boardPointsFoundMap = boardPointsFound.stream()
                .collect(Collectors.toMap(
                        com.rota.facil.transport_service.persistence.entities.BoardPointEntity::getId,
                        boardPoint -> boardPoint
                ));

        if (boardPointsIds.size() != boardPointsFoundMap.size()) throw new BoardPointNotFoundException("Erro ao encontrar pontos de embarque selecionadas. Selecione apenas ponstos de embarque existentes");

        List<BoardPointRouteEntity> boardPointRoutes = new ArrayList<>();

        for (UUID boardPointId : boardPointsIds) {
            BoardPointEntity boardPointFoundMap = boardPointsFoundMap.get(boardPointId);
            CreateBoardPointRouteRequestDTO boardPointFoundRequest = boardPointsRequestMap.get(boardPointId);

            boardPointRoutes.add(
                    BoardPointRouteEntity.builder()
                            .route(route)
                            .boardPoint(boardPointFoundMap)
                            .boardTimeGoing(boardPointFoundRequest.boardTimeGoing())
                            .boardTimeFinish(boardPointFoundRequest.boardTimeFinish())
                            .build()
            );
        }

        if (route.getBoardPoints() == null) route.setBoardPoints(new ArrayList<>());

        route.getBoardPoints().addAll(boardPointRoutes);

        return routeRepository.save(route);
    }

    public RouteHeatMapResponseDTO generateRouteBoardPointHeatMap(UUID routeId, CurrentUser currentUser) {
        RouteEntity routeFound = routeRepository.findByIdAndPrefectureId(routeId, currentUser.prefectureId())
                        .orElseThrow(RouteNotFoundException::new);

        List<PointRequestDTO> points = tripUserRepository.findAllBoardPointsOfTripsFinishedByRouteId(routeId)
                        .stream()
                        .map(boardPoint -> new PointRequestDTO(boardPoint.getLatitude(), boardPoint.getLongitude()))
                        .toList();

        return intelligenceHttpClient.generateRouteHeatMap(routeMapper.map(routeFound.getId(), points, currentUser));
    }

    @Transactional
    public RouteResponseDTO update(UUID routeId, CurrentUser currentUser, UpdateRouteRequestDTO request) {
        RouteEntity routeFound = this.fetchEntity(routeId, currentUser.prefectureId());

        if (routeRepository.countTripsStartedById(routeId) > 0) throw new NotAllowedToUpdateRouteException("Não é permitido atualizar rota porque ainda existem viagens em andamento nessa rota atual");

        routeFound.setName(request.name());
        routeFound.setShift(request.shift());
        routeFound.setGoing(request.going());
        routeFound.setReturn_(request.return_());
        routeFound.setGoingFinish(request.goingFinish());
        routeFound.setReturnFinish(request.returnFinish());

        this.updateInstitution(routeFound, request.institutionsIds().stream().toList());
        this.updateDaysOfWeek(routeFound, request.daysOfWeek());
        this.updateBoardPoints(routeFound, request.boardPoints());
        this.updateRecurringBus(routeFound, request.busIds(), currentUser);

        return routeMapper.map(routeRepository.save(routeFound));
    }

    @Transactional
    public void delete(UUID routeId, CurrentUser currentUser) {
        RouteEntity routeFound = routeRepository.findAnyByIdAndPrefectureId(routeId, currentUser.prefectureId())
                .orElseThrow(RouteNotFoundException::new);

        if (Boolean.FALSE.equals(routeFound.getActive())) {
            throw new RouteNotFoundException();
        }

        if (routeRepository.countTripsStartedById(routeId) > 0) {
            throw new NotAllowedToUpdateRouteException("Não é permitido deletar rota porque ainda existem viagens em andamento nessa rota atual");
        }

        routeRecurringRepository.deleteAllByRoute_Id(routeFound.getId());
        if (routeFound.getRecurring() != null) {
            routeFound.getRecurring().clear();
        }
        routeFound.deactivate();
        routeRepository.save(routeFound);
    }


    private void updateRecurringBus(RouteEntity route, List<UUID> requestedIds, CurrentUser currentUser) {
        List<RouteRecurringEntity> currentRecurring = route.getRecurring();

        if (currentRecurring == null) {
            currentRecurring = new ArrayList<>();
            route.setRecurring(currentRecurring);
        }

        List<RouteRecurringEntity> recurring = currentRecurring;

        Set<UUID> requestedIdSet = new HashSet<>(requestedIds);

        if (requestedIdSet.isEmpty()) {
            throw new BusNotFoundException("Selecione pelo menos um ônibus para a recorrência da rota");
        }

        List<BusEntity> requestedBus = busRepository.findAllActiveByIdInAndPrefectureId(requestedIds, currentUser.prefectureId());

        if (requestedBus.size() != requestedIdSet.size()) {
            throw new BusNotFoundException("Erro ao encontrar ônibus selecionado. Selecione apenas ônibus existentes");
        }

        if (requestedBus.stream().anyMatch(bus -> bus.getDriver() == null)) {
            throw new BusWithoutDriverException("Não é possível criar rota com ônibus sem motorista vinculado");
        }

        Set<UUID> currentIds = recurring.stream()
                .map(RouteRecurringEntity::getBus)
                .filter(Objects::nonNull)
                .map(BusEntity::getId)
                .collect(Collectors.toSet());

        Set<UUID> idsToRemove = currentIds.stream()
                .filter(id -> !requestedIdSet.contains(id))
                .collect(Collectors.toSet());

        Set<UUID> idsToAdd = requestedIdSet.stream()
                .filter(id -> !currentIds.contains(id))
                .collect(Collectors.toSet());

        recurring.removeIf(routeRecurring ->
                routeRecurring.getBus() != null && idsToRemove.contains(routeRecurring.getBus().getId())
        );

        Map<UUID, BusEntity> requestedBusById = requestedBus.stream()
                .collect(Collectors.toMap(BusEntity::getId, Function.identity()));

        idsToAdd.forEach(id -> recurring.add(
                RouteRecurringEntity.builder()
                        .route(route)
                        .bus(requestedBusById.get(id))
                        .build()
        ));
    }

    private void updateInstitution(RouteEntity route, List<UUID> requestedIds) {
        Set<InstitutionEntity> currentInstitutions = route.getInstitutions();

        Set<UUID> currentIds = currentInstitutions.stream()
                .map(InstitutionEntity::getId)
                .collect(Collectors.toSet());

        Set<UUID> idsToRemove = currentIds.stream()
                .filter(id -> !requestedIds.contains(id))
                .collect(Collectors.toSet());

        Set<UUID> idsToAdd = requestedIds.stream()
                .filter(id -> !currentIds.contains(id))
                .collect(Collectors.toSet());

        currentInstitutions.removeIf(i -> idsToRemove.contains(i.getId()));

        if (!idsToAdd.isEmpty()) {

            List<InstitutionEntity> institutions =
                    institutionRepository.findAllActiveByIdIn(idsToAdd);

            if (institutions.size() != idsToAdd.size()) {
                throw new InstitutionNotFoundException("Uma ou mais instituições não foram encontradas.");
            }

            currentInstitutions.addAll(institutions);
        }
    }

    private void updateDaysOfWeek(RouteEntity route,
                                  List<DaysOfWeek> days) {

        route.setDaysOfWeek(new HashSet<>(days));
    }

    private void updateBoardPoints(
            RouteEntity route,
            List<CreateBoardPointRouteRequestDTO> requestBoardPoints
    ) {

        List<BoardPointRouteEntity> currentBoardPoints = route.getBoardPoints();

        Map<UUID, BoardPointRouteEntity> currentByBoardPointId =
                currentBoardPoints.stream()
                        .collect(Collectors.toMap(
                                bp -> bp.getBoardPoint().getId(),
                                Function.identity()
                        ));

        Map<UUID, CreateBoardPointRouteRequestDTO> requestedByBoardPointId =
                requestBoardPoints.stream()
                        .collect(Collectors.toMap(
                                CreateBoardPointRouteRequestDTO::boardPointId,
                                Function.identity()
                        ));

        Set<UUID> currentIds = currentByBoardPointId.keySet();
        Set<UUID> requestedIds = requestedByBoardPointId.keySet();

        Set<UUID> idsToAdd = requestedIds.stream()
                .filter(id -> !currentIds.contains(id))
                .collect(Collectors.toSet());

        Set<UUID> idsToRemove = currentIds.stream()
                .filter(id -> !requestedIds.contains(id))
                .collect(Collectors.toSet());

        // Atualiza os existentes
        requestedIds.stream()
                .filter(currentIds::contains)
                .forEach(id -> {

                    BoardPointRouteEntity entity = currentByBoardPointId.get(id);
                    CreateBoardPointRouteRequestDTO dto = requestedByBoardPointId.get(id);

                    entity.setBoardTimeGoing(dto.boardTimeGoing());
                    entity.setBoardTimeFinish(dto.boardTimeFinish());
                });

        currentBoardPoints.removeIf(bp ->
                idsToRemove.contains(bp.getBoardPoint().getId())
        );

        if (!idsToAdd.isEmpty()) {

            List<BoardPointEntity> boardPoints =
                    boardPointRepository.findAllActiveByIdIn(idsToAdd);

            if (boardPoints.size() != idsToAdd.size()) {
                throw new BoardPointNotFoundException("Um ou mais pontos de embarque não foram encontrados.");
            }

            Map<UUID, BoardPointEntity> boardPointMap =
                    boardPoints.stream()
                            .collect(Collectors.toMap(
                                    BoardPointEntity::getId,
                                    Function.identity()
                            ));

            idsToAdd.forEach(id -> {

                CreateBoardPointRouteRequestDTO dto =
                        requestedByBoardPointId.get(id);

                BoardPointRouteEntity entity =
                        BoardPointRouteEntity.builder()
                                .route(route)
                                .boardPoint(boardPointMap.get(id))
                                .boardTimeGoing(dto.boardTimeGoing())
                                .boardTimeFinish(dto.boardTimeFinish())
                                .build();

                currentBoardPoints.add(entity);
            });
        }
    }


    private RouteEntity fetchEntity(UUID routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(RouteNotFoundException::new);
    }

    private RouteEntity fetchEntity(UUID routeId, UUID prefectureId) {
        return routeRepository.findByIdAndPrefectureId(routeId, prefectureId)
                .orElseThrow(RouteNotFoundException::new);
    }
}
