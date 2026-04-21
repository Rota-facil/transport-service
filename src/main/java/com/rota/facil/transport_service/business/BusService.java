package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.exceptions.BusNotFoundException;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusService {
    private final BusRepository busRepository;
    private final UserRepository userRepository;
    private final BusMapper busMapper;

    public BusResponseDTO register(CreateBusRequestDTO request, CurrentUser currentUser) {
        UserEntity driverFound = userRepository.findDriverById(request.driverId())
                .orElseThrow(UserNotFoundException::new);

        BusEntity preSaved = busMapper.map(request);

        preSaved.setPrefectureId(currentUser.prefectureId());
        preSaved.setDriver(driverFound);

        return busMapper.map(busRepository.save(preSaved));
    }

    public BusResponseDTO fetch(UUID busId) {
        return busMapper.map(this.fetchEntity(busId));
    }

    public List<BusResponseDTO> list() {
        return busRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(busMapper::map)
                .toList();
    }

    private BusEntity fetchEntity(UUID busId) {
        return busRepository.findById(busId)
                .orElseThrow(BusNotFoundException::new);
    }
}
