package com.rota.facil.transport_service.messaging.consumers;

import com.rota.facil.transport_service.business.UserService;
import com.rota.facil.transport_service.messaging.dto.receive.UserEventReceive;
import com.rota.facil.transport_service.messaging.mappers.UserEventMapper;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitAuthEventConsumer {
    private final UserService userService;
    private final UserEventMapper userEventMapper;

    @RabbitListener(queues = "${rabbitmq.transport.user.created.queue}")
    public void handlerCreateUser(UserEventReceive createUser) {
        try {
            UserEntity userEntity = userEventMapper.map(createUser);
            userService.register(userEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "${rabbitmq.transport.user.updated.queue}")
    public void handlerUpdateUser(UserEventReceive updateUser) {
        try {
            UserEntity userEntity = userEventMapper.map(updateUser);
            userService.update(userEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "${rabbitmq.transport.user.deleted.queue}")
    public void handlerDeleteUser(UserEventReceive updateUser) {
        try {
            UserEntity userEntity = userEventMapper.map(updateUser);
            userService.delete(userEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
