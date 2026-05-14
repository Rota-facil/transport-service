package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.enums.DaysOfWeek;
import com.rota.facil.transport_service.domain.exceptions.*;
import com.rota.facil.transport_service.http.client.IntelligenceHttpClient;
import com.rota.facil.transport_service.http.client.mappers.IntelligenceMapper;
import com.rota.facil.transport_service.http.dto.request.route.CreateBoardPointRouteRequestDTO;
import com.rota.facil.transport_service.http.dto.request.route.CreateRouteRecurringBusRequestDTO;
import com.rota.facil.transport_service.http.dto.request.route.CreateRouteRequestDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.client.intelligence.RouteInterpretationResponseDTO;
import com.rota.facil.transport_service.http.dto.response.route.RouteResponseDTO;
import com.rota.facil.transport_service.persistence.entities.*;
import com.rota.facil.transport_service.persistence.mappers.RouteMapper;
import com.rota.facil.transport_service.persistence.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
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

    @Transactional
    public RouteResponseDTO register(CreateRouteRequestDTO request, CurrentUser currentUser) {
        Set<InstitutionEntity> institutionsFound = institutionRepository.findAllSetById(request.institutionsIds());
        List<BusEntity> busListFound = busRepository.findAllById(request.busIds());

        if (institutionsFound.size() != request.institutionsIds().size()) throw new InstitutionNotFoundException("Erro ao encontrar instituições selecionadas. Selecione apenas instituições existentes");
        if (busListFound.size() != request.busIds().size()) throw new BusNotFoundException("Erro ao encontrar ônibus selecionado. Selecione apenas ônibus existentes");

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

        if (request.daysOfWeek().contains(DaysOfWeek.getFromValueDay(LocalDate.now().getDayOfWeek().getValue()))) {

            List<TripEntity> tripEntities = new ArrayList<>();

            for (BusEntity bus : busListFound) {
                TripEntity createdTrip = TripEntity.builder()
                        .route(saved)
                        .bus(bus)
                        .prefectureId(currentUser.prefectureId())
                        .build();
                createdTrip.setTripStatus(new ArrayList<>());
                createdTrip.getTripStatus().add(TripStatusEntity.builder().trip(createdTrip).build());

                tripEntities.add(createdTrip);
            }

            tripRepository.saveAll(tripEntities);
        }

        return routeMapper.map(saved);
    }

    public RouteResponseDTO fetch(UUID routeId, CurrentUser currentUser) {
        return routeMapper.map(this.fetchEntity(routeId, currentUser.prefectureId()));
    }

    public List<RouteResponseDTO> list(CurrentUser currentUser) {
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

    public RouteResponseDTO addBoardPoints(UUID routeId, List<CreateBoardPointRouteRequestDTO> request, CurrentUser currentUser) {
        RouteEntity routeFound = this.fetchEntity(routeId, currentUser.prefectureId());

        List<UUID> boardPointsIds = request.stream().map(CreateBoardPointRouteRequestDTO::boardPointId).toList();

        List<BoardPointEntity> boardPointsFound = boardPointRepository.findAllById(boardPointsIds);

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
                            .route(routeFound)
                            .boardPoint(boardPointFoundMap)
                            .boardTimeGoing(boardPointFoundRequest.boardTimeGoing())
                            .boardTimeFinish(boardPointFoundRequest.boardTimeFinish())
                            .build()
            );
        }

        if (routeFound.getBoardPoints() == null) routeFound.setBoardPoints(new ArrayList<>());

        routeFound.getBoardPoints().addAll(boardPointRoutes);

        return routeMapper.map(routeRepository.save(routeFound));
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
