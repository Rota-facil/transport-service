package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.enums.BusStatus;
import com.rota.facil.transport_service.domain.enums.DriverStatus;
import com.rota.facil.transport_service.domain.enums.Role;
import com.rota.facil.transport_service.domain.exceptions.BusInOperationExceptions;
import com.rota.facil.transport_service.domain.exceptions.BusNotFoundException;
import com.rota.facil.transport_service.domain.exceptions.DriverIsOnRouteException;
import com.rota.facil.transport_service.domain.exceptions.UserNotFoundException;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.request.user.UpdateBusOfDriverRequestDTO;
import com.rota.facil.transport_service.http.dto.response.user.DriverResponseDTO;
import com.rota.facil.transport_service.persistence.entities.BusEntity;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import com.rota.facil.transport_service.persistence.mappers.UserMapper;
import com.rota.facil.transport_service.persistence.repositories.BusRepository;
import com.rota.facil.transport_service.persistence.repositories.TripRepository;
import com.rota.facil.transport_service.persistence.repositories.TripUserRepository;
import com.rota.facil.transport_service.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final TripUserRepository tripUserRepository;
    private final UserMapper userMapper;

    public void register(UserEntity user) {
        user.moveToAvailable();
        userRepository.save(user);
    }

    public void update(UserEntity userEntity) {
        UserEntity userFound = this.fetchEntity(userEntity.getId());
        userFound.update(userEntity);
        userRepository.save(userFound);
    }

    public void updateBusOfDriver(UpdateBusOfDriverRequestDTO request, CurrentUser currentUser, UUID driverId) {
        UserEntity driverFound = userRepository.findDriverByIdAndPrefectureId(driverId, currentUser.prefectureId())
                .orElseThrow(UserNotFoundException::new);
        BusEntity busFound = busRepository.findByIdAndPrefectureId(request.busId(), currentUser.prefectureId())
                .orElseThrow(BusNotFoundException::new);

        if (busFound.getStatus().equals(BusStatus.OPERATION)) throw new BusInOperationExceptions("Nao é possível trocar ônibus do motorista pois o ônibus está em operação");
        if (driverFound.getStatus().equals(DriverStatus.ON_ROUTE)) throw new DriverIsOnRouteException("Não é possível trocar ônibus do motorista pois no momento ele está em rota");

        if (driverFound.getBus() == null || !driverFound.getBus().getId().equals(request.busId())) {
            busFound.setDriver(driverFound);
            driverFound.setBus(busFound);

            busRepository.save(busFound);
            userRepository.save(driverFound);
        }
    }

    public void delete(UserEntity userEntity) {
        userRepository.deleteById(userEntity.getId());
    }

    private UserEntity fetchEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public void deactivate(UserEntity userEntity) {
        this.removeTravelUser(userEntity);
    }

    private void removeTravelUser(UserEntity userEntity) {
        tripUserRepository.deleteByUserId(userEntity.getId());

//        Criar lógica para se o usuario quiser sair da viagem mas a viagem ja foi iniciada, colocar ele na lista de faltas
    }

    public List<DriverResponseDTO> listDrivers(CurrentUser currentUser) {
        return userRepository.findAllDriversByPrefectureId(currentUser.prefectureId())
                .stream()
                .map(userMapper::mapToDriver)
                .toList();
    }
}
