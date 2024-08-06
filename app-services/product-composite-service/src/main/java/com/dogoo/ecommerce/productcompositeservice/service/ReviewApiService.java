package com.dogoo.ecommerce.productcompositeservice.service;


import com.dogoo.ecommerce.productcompositeservice.config.ServiceProperties;
import com.dogoo.ecommerce.review.client.ApiClient;
import com.dogoo.ecommerce.review.client.api.ReviewApi;
import com.dogoo.ecommerce.review.client.model.Review;
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
public class ReviewApiService {

    private final Logger LOG = LoggerFactory.getLogger(ReviewApiService.class);

    private final ServiceProperties properties;

    private final HandlerUtils utils;

    private final WebClient webClient;

    public ReviewApiService(ServiceProperties properties,
                            HandlerUtils utils,
                            WebClient webClient) {
        this.properties = properties;
        this.utils = utils;
        this.webClient = webClient;
    }

    public Flux<Review> getReviewsByProductId(String productId) {

        ApiClient apiClient = new ApiClient(webClient);

        String port = properties.getProduct().getPort() == 0 ? "" : ":" + properties.getProduct().getPort();

        apiClient.setBasePath(properties.getReview().getScheme() + "://" +
                properties.getReview().getHost() + port + "/api/v1");

        ReviewApi reviewApi = new ReviewApi(apiClient);

        String EMPTY_STR = "";
        return reviewApi.getReviews(productId,
                        EMPTY_STR,
                0,
                0,
                        EMPTY_STR,
                        EMPTY_STR)
                .log()
                .onErrorMap(WebClientResponseException.class, e -> utils.handleException(e));
    }

    public Mono<Health> getReviewHealth() {
        
        String port = properties.getProduct().getPort() == 0 ? "" : ":" + properties.getProduct().getPort();

        String url = properties.getReview().getScheme() + "://" +
                     properties.getReview().getHost() + port + "/actuator/health";
        
        LOG.debug("Will call the Health API on URL: {}", url);
        
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
            .map(s -> new Health.Builder().up().build())
            .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
            .log(LOG.getName(), Level.FINE);
    }
}
