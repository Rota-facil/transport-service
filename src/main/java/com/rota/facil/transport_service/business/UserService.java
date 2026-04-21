package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.exceptions.UserNotFoundException;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import com.rota.facil.transport_service.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void register(UserEntity user) {
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
}
