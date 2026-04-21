package com.rota.facil.transport_service.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Value("${rabbitmq.transport.exchange}")
    private String transportExchange;

    @Value("${rabbitmq.auth.exchange}")
    private String authExchange;

    @Value("${rabbitmq.places.exchange}")
    private String placesExchange;


    @Value("${rabbitmq.transport.user.created.queue}")
    private String userCreatedQueue;

    @Value("${rabbitmq.transport.user.updated.queue}")
    private String userUpdatedQueue;

    @Value("${rabbitmq.transport.user.deleted.queue}")
    private String userDeletedQueue;

    @Value("${rabbitmq.user.created.routing.key}")
    private String userCreatedRoutingKey;

    @Value("${rabbitmq.user.updated.routing.key}")
    private String userUpdatedRoutingKey;

    @Value("${rabbitmq.user.deleted.routing.key}")
    private String userDeletedRoutingKey;




    @Value("${rabbitmq.transport.institution.created.queue}")
    private String institutionCreatedQueue;

    @Value("${rabbitmq.transport.institution.updated.queue}")
    private String institutionUpdatedQueue;

    @Value("${rabbitmq.transport.institution.deleted.queue}")
    private String institutionDeletedQueue;

    @Value("${rabbitmq.institution.created.routing.key}")
    private String institutionCreatedRoutingKey;

    @Value("${rabbitmq.institution.updated.routing.key}")
    private String institutionUpdatedRoutingKey;

    @Value("${rabbitmq.institution.deleted.routing.key}")
    private String institutionDeletedRoutingKey;


    @Value("${rabbitmq.transport.board.point.created.queue}")
    private String boardPointCreatedQueue;

    @Value("${rabbitmq.transport.board.point.updated.queue}")
    private String boardPointUpdatedQueue;

    @Value("${rabbitmq.transport.board.point.deleted.queue}")
    private String boardPointDeletedQueue;

    @Value("${rabbitmq.board.point.created.routing.key}")
    private String boardPointCreatedRoutingKey;

    @Value("${rabbitmq.board.point.updated.routing.key}")
    private String boardPointUpdatedRoutingKey;

    @Value("${rabbitmq.board.point.deleted.routing.key}")
    private String boardPointDeletedRoutingKey;


    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitListener(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        simpleRabbitListenerContainerFactory.setMessageConverter(messageConverter);
        return simpleRabbitListenerContainerFactory;
    }

    @Bean
    public TopicExchange transportExchange() {
        return new TopicExchange(transportExchange);
    }

    @Bean
    public TopicExchange placesExchange() {
        return new TopicExchange(placesExchange);
    }

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(authExchange);
    }

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(userCreatedQueue);
    }

    @Bean
    public Queue userUpdatedQueue() {
        return new Queue(userUpdatedQueue);
    }

    @Bean
    public Queue userDeletedQueue() {
        return new Queue(userDeletedQueue);
    }


    @Bean
    public Queue institutionCreatedQueue() {
        return new Queue(institutionCreatedQueue);
    }

    @Bean
    public Queue institutionUpdatedQueue() {
        return new Queue(institutionUpdatedQueue);
    }

    @Bean
    public Queue institutionDeletedQueue() {
        return new Queue(institutionDeletedQueue);
    }

    @Bean
    public Queue boardPointCreatedQueue() {
        return new Queue(boardPointCreatedQueue);
    }

    @Bean
    public Queue boardPointUpdatedQueue() {
        return new Queue(boardPointUpdatedQueue);
    }

    @Bean
    public Queue boardPointDeletedQueue() {
        return new Queue(boardPointDeletedQueue);
    }


    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(this.userCreatedQueue()).to(this.authExchange()).with(userCreatedRoutingKey);
    }

    @Bean
    public Binding userUpdatedBinding() {
        return BindingBuilder.bind(this.userUpdatedQueue()).to(this.authExchange()).with(userUpdatedRoutingKey);
    }

    @Bean
    public Binding userDeletedBinding() {
        return BindingBuilder.bind(this.userDeletedQueue()).to(this.authExchange()).with(userDeletedRoutingKey);
    }


    @Bean
    public Binding institutionCreatedBinding() {
        return BindingBuilder.bind(this.institutionCreatedQueue()).to(this.placesExchange()).with(institutionCreatedRoutingKey);
    }

    @Bean
    public Binding institutionUpdatedBinding() {
        return BindingBuilder.bind(this.institutionUpdatedQueue()).to(this.placesExchange()).with(institutionUpdatedRoutingKey);
    }

    @Bean
    public Binding institutionDeletedBinding() {
        return BindingBuilder.bind(this.institutionCreatedQueue()).to(this.placesExchange()).with(institutionDeletedRoutingKey);
    }


    @Bean
    public Binding boardPointCreatedBinding() {
        return BindingBuilder.bind(this.boardPointCreatedQueue()).to(this.placesExchange()).with(boardPointCreatedRoutingKey);
    }

    @Bean
    public Binding boardPointUpdatedBinding() {
        return BindingBuilder.bind(this.boardPointUpdatedQueue()).to(this.placesExchange()).with(boardPointUpdatedRoutingKey);
    }

    @Bean
    public Binding boardPointDeletedBinding() {
        return BindingBuilder.bind(this.boardPointDeletedQueue()).to(this.placesExchange()).with(boardPointDeletedRoutingKey);
    }
}
