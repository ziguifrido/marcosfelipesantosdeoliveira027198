package dev.marcosoliveira.discography.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Component
public class RegionalClient {

    private final RestTemplate restTemplate;

    @Value("${external.api.regional.url}")
    private String baseUrl;

    public RegionalClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<RegionalExternalDTO> fetchAllRegionals() {
        return Objects.requireNonNull(restTemplate.exchange(
                baseUrl + "/v1/regionais",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RegionalExternalDTO>>() {}
        ).getBody());
    }
}
