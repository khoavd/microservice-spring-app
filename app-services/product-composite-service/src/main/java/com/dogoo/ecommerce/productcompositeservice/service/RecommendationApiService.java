package com.dogoo.ecommerce.productcompositeservice.service;


import com.dogoo.ecommerce.productcompositeservice.config.ServiceProperties;

import com.dogoo.ecommerce.recommendation.client.ApiClient;
import com.dogoo.ecommerce.recommendation.client.api.RecommendationApi;
import com.dogoo.ecommerce.recommendation.client.model.Recommendation;
import com.dogoo.exception.HandlerUtils;

import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RecommendationApiService {

    private final Logger LOG = LoggerFactory.getLogger(RecommendationApiService.class);

    private final ServiceProperties properties;

    private final HandlerUtils utils;

    private final WebClient webClient;

    public RecommendationApiService(ServiceProperties properties, 
                                    HandlerUtils utils,
                                    WebClient webClient) {
        this.properties = properties;
        this.utils = utils;
        this.webClient = webClient;
    }

    public Flux<Recommendation> getRecommendationsByProductId(String productId) {

        ApiClient apiClient = new ApiClient(webClient);

        String port = properties.getProduct().getPort() == 0 ? "" : ":" + properties.getProduct().getPort();

        apiClient.setBasePath(properties.getRecommendation().getScheme() + "://" +
                properties.getRecommendation().getHost() + port + "/api/v1");

        RecommendationApi api = new RecommendationApi(apiClient);

        String EMPTY_STR = "";
        return api.getRecommendations(
                productId,
                EMPTY_STR,
                0,
                0,
                EMPTY_STR,
                EMPTY_STR).onErrorMap(WebClientResponseException.class, e -> utils.handleException(e));

    }

    public Mono<Health> getRecommendationsHealth() {
        String port = properties.getProduct().getPort() == 0 ? "" : ":" + properties.getProduct().getPort();

        String url = properties.getRecommendation().getScheme() + "://" +
                     properties.getRecommendation().getHost() + port + "/actuator/health";
        
        LOG.debug("Will call the Health API on URL: {}", url);
        
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
            .map(s -> new Health.Builder().up().build())
            .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
            .log(LOG.getName(), Level.FINE);
    }
}
