package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.enums.DriverStatus;
import com.rota.facil.transport_service.domain.enums.Role;
import com.rota.facil.transport_service.domain.exceptions.UserNotFoundException;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.user.DriverResponseDTO;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import com.rota.facil.transport_service.persistence.mappers.UserMapper;
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
    private final TripUserRepository tripUserRepository;
    private final UserMapper userMapper;

    public void register(UserEntity user) {
        user.moveToAvailable();
        userRepository.save(user);
    }

    public void update(UserEntity userEntity) {
        UserEntity userFound = this.fetchEntity(userEntity.getId());
        userFound.update(userEntity);
        userRepository.save(userEntity);
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
