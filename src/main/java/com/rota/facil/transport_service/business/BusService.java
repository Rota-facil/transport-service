package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.enums.BusStatus;
import com.rota.facil.transport_service.domain.enums.DriverStatus;
import com.rota.facil.transport_service.domain.exceptions.BusInOperationExceptions;
import com.rota.facil.transport_service.domain.exceptions.BusNotFoundException;
import com.rota.facil.transport_service.domain.exceptions.DriverAlreadyHasBusException;
import com.rota.facil.transport_service.domain.exceptions.DriverIsOnRouteException;
import com.rota.facil.transport_service.domain.exceptions.UserNotFoundException;
import com.rota.facil.transport_service.http.dto.request.bus.CreateBusRequestDTO;
import com.rota.facil.transport_service.http.dto.request.bus.UpdateBusRequestDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.bus.BusResponseDTO;
import com.rota.facil.transport_service.messaging.producers.RabbitTransportBusEventProducer;
import com.rota.facil.transport_service.persistence.entities.BusEntity;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import com.rota.facil.transport_service.persistence.mappers.BusMapper;
import com.rota.facil.transport_service.persistence.repositories.BusRepository;
import com.rota.facil.transport_service.persistence.repositories.RouteRecurringRepository;
import com.rota.facil.transport_service.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusService {
    private final BusRepository busRepository;
    private final UserRepository userRepository;
    private final RouteRecurringRepository routeRecurringRepository;
    private final RabbitTransportBusEventProducer busEventProducer;
    private final BusMapper busMapper;

    @Transactional
    public BusResponseDTO register(CreateBusRequestDTO request, CurrentUser currentUser) {
        UserEntity driverFound = null;

        if (request.driverId() != null) {
            driverFound = userRepository.findDriverByIdAndPrefectureId(request.driverId(), currentUser.prefectureId())
                    .orElseThrow(UserNotFoundException::new);

            if (driverFound.getStatus().equals(DriverStatus.ON_ROUTE)) throw new DriverIsOnRouteException("Não é possível vincular motorista pois no momento ele está em rota");

            BusEntity oldBus = busRepository.findByDriverId(request.driverId())
                    .orElse(null);

            if (oldBus != null) {
                throw new DriverAlreadyHasBusException();
            }
        }

        BusEntity preSaved = busMapper.map(request);

        preSaved.setPrefectureId(currentUser.prefectureId());
        preSaved.setDriver(driverFound);

        return busMapper.map(busRepository.save(preSaved));
    }

    @Transactional
    public BusResponseDTO update(UUID busId, UpdateBusRequestDTO request, CurrentUser currentUser) {
        BusEntity busFound = this.fetchEntityByPrefectureId(busId, currentUser.prefectureId());

        if (request.plate() != null) {
            busFound.setPlate(request.plate());
        }
        if (request.capacity() != null) {
            busFound.setCapacity(request.capacity());
        }
        if (request.status() != null) {
            busFound.setStatus(request.status());
        }

        updateBusDriver(busFound, request.driverId(), currentUser);

        return busMapper.map(busRepository.save(busFound));
    }

    private void updateBusDriver(BusEntity busFound, UUID driverId, CurrentUser currentUser) {
        UUID currentDriverId = busFound.getDriver() != null ? busFound.getDriver().getId() : null;

        if (currentDriverId == null && driverId == null) {
            return;
        }

        if (currentDriverId != null && currentDriverId.equals(driverId)) {
            return;
        }

        if (busFound.getStatus().equals(BusStatus.OPERATION)) throw new BusInOperationExceptions("Nao é possível alterar motorista pois o ônibus está em operação");

        UserEntity currentDriver = busFound.getDriver();

        if (driverId == null) {
            if (currentDriver != null) {
                currentDriver.setBus(null);
            }
            busFound.setDriver(null);
            return;
        }

        UserEntity driverFound = userRepository.findDriverByIdAndPrefectureId(driverId, currentUser.prefectureId())
                .orElseThrow(UserNotFoundException::new);

        if (driverFound.getStatus().equals(DriverStatus.ON_ROUTE)) throw new DriverIsOnRouteException("Não é possível vincular motorista pois no momento ele está em rota");

        BusEntity driverCurrentBus = busRepository.findByDriverId(driverId).orElse(null);
        if (driverCurrentBus != null && !driverCurrentBus.getId().equals(busFound.getId())) {
            throw new DriverAlreadyHasBusException();
        }

        if (currentDriver != null) {
            currentDriver.setBus(null);
        }

        busFound.setDriver(driverFound);
        driverFound.setBus(busFound);
    }

    public BusResponseDTO fetch(UUID busId, CurrentUser currentUser) {
        return busMapper.map(this.fetchEntityByPrefectureId(busId, currentUser.prefectureId()));
    }

    @Transactional
    public void delete(UUID busId, CurrentUser currentUser) {
        BusEntity busFound = busRepository.findAnyByIdAndPrefectureId(busId, currentUser.prefectureId())
                .orElseThrow(BusNotFoundException::new);

        if (busFound.getStatus().equals(BusStatus.OPERATION)) throw new BusInOperationExceptions("Nao é possível deletar ônibus pois ele está em operação");

        UserEntity driver = busFound.getDriver();
        if (driver != null) {
            driver.setBus(null);
            busFound.setDriver(null);
        }

        routeRecurringRepository.deleteAllByBus_Id(busFound.getId());
        busFound.deactivate();
        BusEntity deletedBus = busRepository.save(busFound);
        busEventProducer.deleteBusEvent(deletedBus);
    }

    public List<BusResponseDTO> list(CurrentUser currentUser) {
        return busRepository.findAllByPrefectureId(currentUser.prefectureId())
                .stream()
                .map(busMapper::map)
                .toList();
    }

    private BusEntity fetchEntity(UUID busId) {
        return busRepository.findById(busId)
                .orElseThrow(BusNotFoundException::new);
    }

    private BusEntity fetchEntityByPrefectureId(UUID busId, UUID prefectureId) {
        return busRepository.findByIdAndPrefectureId(busId, prefectureId)
                .orElseThrow(BusNotFoundException::new);
    }
}
