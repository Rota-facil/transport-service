package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.enums.BusStatus;
import com.rota.facil.transport_service.domain.enums.DriverStatus;
import com.rota.facil.transport_service.domain.exceptions.BusInOperationExceptions;
import com.rota.facil.transport_service.domain.exceptions.BusNotFoundException;
import com.rota.facil.transport_service.domain.exceptions.DriverIsOnRouteException;
import com.rota.facil.transport_service.domain.exceptions.UserNotFoundException;
import com.rota.facil.transport_service.http.dto.request.bus.CreateBusRequestDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.bus.BusResponseDTO;
import com.rota.facil.transport_service.persistence.entities.BusEntity;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import com.rota.facil.transport_service.persistence.mappers.BusMapper;
import com.rota.facil.transport_service.persistence.repositories.BusRepository;
import com.rota.facil.transport_service.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusService {
    private final BusRepository busRepository;
    private final UserRepository userRepository;
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
                if (oldBus.getStatus().equals(BusStatus.OPERATION)) throw new BusInOperationExceptions("Nao é possível trocar motorista pois o ônibus atual está em operação");
                oldBus.setDriver(null);
                busRepository.save(oldBus);
            }
        }

        BusEntity preSaved = busMapper.map(request);

        preSaved.setPrefectureId(currentUser.prefectureId());
        preSaved.setDriver(driverFound);

        return busMapper.map(busRepository.save(preSaved));
    }

    public BusResponseDTO fetch(UUID busId, CurrentUser currentUser) {
        return busMapper.map(this.fetchEntityByPrefectureId(busId, currentUser.prefectureId()));
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
