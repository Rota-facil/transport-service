package com.rota.facil.transport_service.http.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rota.facil.transport_service.http.client.IntelligenceHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Configuration
public class RestClientConfig {
    @Value("${intelligence.base.url}")
    private String intelligenceBaseUrl;

    @Bean
    public IntelligenceHttpClient intelligenceHttpClient(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10 * 1000);
        requestFactory.setReadTimeout(660 * 1000);

        RestClient restClient = restClientBuilder
                .baseUrl(intelligenceBaseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("Content-Type", "application/json")
                .defaultStatusHandler(status -> status.is4xxClientError(), (request, response) -> {
                    String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                    String message = extractErrorMessage(body, objectMapper);
                    int statusCode = response.getStatusCode().value();
                    throw new RuntimeException(message);
                })
                .build();

        HttpServiceProxyFactory proxyFactory =HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return proxyFactory.createClient(IntelligenceHttpClient.class);
    }

    private String extractErrorMessage(String body, ObjectMapper objectMapper) {
        if (body == null || body.isBlank()) {
            return "Erro ao processar requisicao";
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode detail = root.path("detail");
            if (detail.isObject()) {
                JsonNode message = detail.path("message");
                if (message.isTextual()) {
                    return message.asText();
                }
            }
            JsonNode message = root.path("message");
            if (message.isTextual()) {
                return message.asText();
            }
        } catch (Exception ignored) {
        }
        return body;
    }
}
