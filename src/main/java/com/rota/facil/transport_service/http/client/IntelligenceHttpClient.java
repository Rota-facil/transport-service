package com.rota.facil.transport_service.http.client;

import com.rota.facil.transport_service.http.dto.request.client.intelligence.RouteInformationRequestDTO;
import com.rota.facil.transport_service.http.dto.response.client.intelligence.RouteInterpretationResponseDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/intelligence")
public interface IntelligenceHttpClient {
    @GetExchange
    String heathCheck();

    @PostExchange("/route/interpretation")
    RouteInterpretationResponseDTO generateRouteInterpretation(@RequestBody RouteInformationRequestDTO request);
}
