package com.dogoo.ecommerce.recommendationservice.api;

import com.dogoo.ecommerce.recommendation.api.RecommendationsApi;
import com.dogoo.ecommerce.recommendation.api.model.Recommendation;
import com.dogoo.ecommerce.recommendationservice.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class RecommendationController implements RecommendationsApi {

    private final Logger log = LoggerFactory.getLogger(RecommendationController.class);
    
    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @Override
    public Mono<ResponseEntity<Flux<Recommendation>>> getRecommendations(String productId,
                                                                         String filter,
                                                                         Integer page,
                                                                         Integer pageSize,
                                                                         String search,
                                                                         String sort,
                                                                         ServerWebExchange exchange) {

        return Mono.just(new ResponseEntity<>(service.getRecommendationsByProductId(productId), HttpStatus.OK));

    }

}
