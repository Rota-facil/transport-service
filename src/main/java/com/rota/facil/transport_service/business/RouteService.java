package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.enums.DaysOfWeek;
import com.rota.facil.transport_service.domain.exceptions.BoardPointNotFoundException;
import com.rota.facil.transport_service.domain.exceptions.BusNotFoundException;
import com.rota.facil.transport_service.domain.exceptions.InstitutionNotFoundException;
import com.rota.facil.transport_service.domain.exceptions.RouteNotFoundException;
import com.rota.facil.transport_service.http.dto.request.route.CreateBoardPointRouteRequestDTO;
import com.rota.facil.transport_service.http.dto.request.route.CreateRouteRecurringRequestDTO;
import com.rota.facil.transport_service.http.dto.request.route.CreateRouteRequestDTO;
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
    private final RouteMapper routeMapper;

    @Transactional
    public RouteResponseDTO register(CreateRouteRequestDTO request) {
        Set<InstitutionEntity> institutionsFound = institutionRepository.findAllSetById(request.institutionsIds());

        if (institutionsFound.size() != request.institutionsIds().size()) throw new InstitutionNotFoundException("Erro ao encontrar instituições selecionadas. Selecione apenas instituições existentes");

        RouteEntity preSaved = routeMapper.map(request);
        preSaved.setInstitutions(institutionsFound);

        CreateRouteRecurringRequestDTO recurringRequest = request.recurring();

        RouteEntity saved = routeRepository.save(preSaved);

        if (recurringRequest.daysOfWeeks().contains(DaysOfWeek.getFromValueDay(LocalDate.now().getDayOfWeek().getValue()))) {
            BusEntity busFound = busRepository.findById(recurringRequest.busId())
                    .orElseThrow(BusNotFoundException::new);

            TripEntity tripSaved = tripRepository.save(
                    TripEntity.builder()
                            .route(saved)
                            .bus(busFound)
                            .build()
            );

            tripSaved.setTripStatus(new ArrayList<>());
            tripSaved.getTripStatus().add(tripStatusRepository.save(TripStatusEntity.builder().trip(tripSaved).build()));
        }

        return routeMapper.map(saved);
    }

    public RouteResponseDTO fetch(UUID routeId) {
        return routeMapper.map(this.fetchEntity(routeId));
    }

    public List<RouteResponseDTO> list() {
        return routeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(routeMapper::map)
                .toList();
    }

    public RouteResponseDTO addBoardPoints(UUID routeId, List<CreateBoardPointRouteRequestDTO> request) {
        RouteEntity routeFound = this.fetchEntity(routeId);

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
}
