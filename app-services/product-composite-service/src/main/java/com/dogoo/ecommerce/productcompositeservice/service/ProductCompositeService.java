package com.dogoo.ecommerce.productcompositeservice.service;


import com.dogoo.ecommerce.product.client.model.Product;
import com.dogoo.ecommerce.product.composite.api.model.ProductComposite;
import com.dogoo.ecommerce.productcompositeservice.mapper.ProductCompositeMapper;
import com.dogoo.ecommerce.productcompositeservice.producer.ProductCompositeProducerService;
import com.dogoo.ecommerce.productcompositeservice.tracing.ObservationUtil;
import com.dogoo.ecommerce.recommendation.client.model.Recommendation;
import com.dogoo.ecommerce.review.client.model.Review;
import com.dogoo.exception.HandlerUtils;
import com.dogoo.exception.model.InternalServerException;
import com.dogoo.exception.model.InvalidInputException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;

@Service
public class ProductCompositeService {

    private final Logger LOG = LoggerFactory.getLogger(ProductCompositeService.class);

    private final ProductCompositeMapper mapper;

    private final ProductApiService productApiService;

    private final ReviewApiService reviewApiService;

    private final RecommendationApiService recommendationApiService;

    private final ProductCompositeProducerService producerService;

    private final HandlerUtils utils;

    private final ObservationUtil observationUtil;

    public ProductCompositeService(ObjectMapper objectMapper,
                                   ProductCompositeMapper mapper,
                                   ProductApiService productApiService,
                                   ReviewApiService reviewApiService,
                                   RecommendationApiService recommendationApiService, ProductCompositeProducerService producerService, HandlerUtils utils, ObservationUtil observationUtil) {
        this.producerService = producerService;
        this.utils = utils;
        this.mapper = mapper;
        this.productApiService = productApiService;
        this.reviewApiService = reviewApiService;
        this.recommendationApiService = recommendationApiService;
        this.observationUtil = observationUtil;
    }

    public Mono<ProductComposite> getProductById(String productId,
                                                 int delay,
                                                 int faultPercent) {
        
        return observationWithProductInfo(productId,
                () -> getProductByIdInternal(productId, delay, faultPercent));

    }

    private Mono<ProductComposite> getProductByIdInternal(String productId,
                                                         int delay,
                                                         int faultPercent) {

        return Mono.just(productId).map(UUID::fromString)
            .onErrorResume(ex -> Mono.error(new InvalidInputException("INVALID_UUID " + ex.getMessage())))
            .flatMap(d -> Mono.zip(
                values -> mapper.mapProductComposite((Product) values[0], (List<Review>) values[1], (List<Recommendation>) values[2]),
                productApiService.getProductById(productId, delay, faultPercent),
                reviewApiService.getReviewsByProductId(productId).collectList(),
                recommendationApiService.getRecommendationsByProductId(productId).collectList())
                .onErrorMap(Exception.class, utils::handleException)
                .log(LOG.getName(), Level.FINE));

    }

    public Mono<ProductComposite> addProductComposite(ProductComposite productComposite) {

        return observationWithProductInfo(productComposite.getId(),
                () -> addProductCompositeInternal(productComposite));
    }

    private Mono<ProductComposite> addProductCompositeInternal(ProductComposite productComposite) {

        if (productComposite.getId() == null
                || productComposite.getId().isBlank()
                || productComposite.getId().isEmpty()) {

            productComposite.setId(UUID.randomUUID().toString());
        }

        try {

            LOG.info("Will create a new composite entity for");

            return Mono.zip(values -> mapper.mapProductComposite((Product) values[0], (List<Review>) values[1], (List<Recommendation>) values[2]),
                            producerService.createProduct(mapper.mapProductFromComposite(productComposite)),
                            Flux.fromIterable(productComposite.getReviews()).flatMap(e -> producerService.createReview(mapper.mapReviewFromComposite(productComposite.getId(), e))).collectList(),
                            Flux.fromIterable(productComposite.getRecommendations()).flatMap(e -> producerService.createRcmd(mapper.mapRecommendationFromComposite(productComposite.getId(), e))).collectList())
                    .onErrorMap(Exception.class, utils::handleException)
                    .log(LOG.getName(), Level.FINE);

        } catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed: {}", re.toString());
            throw re;
        }

    }

    public Mono<Void> deleteProduct(String productId) {
        return observationWithProductInfo(productId, () -> deleteProductInternal(productId));
    }

    private Mono<Void> deleteProductInternal(String productId) {
        try {
            LOG.info("Will delete product has id {}", productId);

            return Mono.just(productId).map(UUID::fromString)
                    .onErrorResume(ex -> Mono.error(new InvalidInputException("INVALID_UUID " + ex.getMessage())))
                    .flatMap(d -> Mono
                    .zip(r -> "",
                            producerService.deleteProduct(productId),
                            producerService.deleteReview(productId),
                            producerService.deleteRcmd(productId))
                    .onErrorMap(Exception.class, utils::handleException)
                    .log(LOG.getName(), Level.FINE).then());

        } catch (RuntimeException re) {
            LOG.warn("deleteCompositeProduct failed: {}", re.toString());
            throw re;
        }
    }

    public Mono<ProductComposite> updateProductComposite(String productId,
                                                         ProductComposite productComposite) {

        return observationWithProductInfo(productId, () -> updateProductCompositeInternal(productId, productComposite));

    }

    private Mono<ProductComposite> updateProductCompositeInternal(String productId,
                                                         ProductComposite productComposite) {

        try {

            LOG.info("Will update a composite entity for");
            return Mono.just(productId).map(UUID::fromString)
                    .onErrorResume(ex -> Mono.error(new InvalidInputException("INVALID_UUID " + ex.getMessage())))
                    .flatMap(d -> {
                        return Mono.zip(values -> mapper.mapProductComposite(
                                                            (Product) values[0],
                                                            (List<Review>) values[1],
                                                            (List<Recommendation>) values[2]),
                                            producerService.updateProduct( productId, mapper.mapProductFromComposite(productComposite)),
                                            Flux.fromIterable(productComposite.getReviews()).flatMap(e -> producerService.updateReview(mapper.mapReviewFromComposite(productId, e))).collectList(),
                                            Flux.fromIterable(productComposite.getRecommendations()).flatMap(e -> producerService.updateRcmd(mapper.mapRecommendationFromComposite(productId, e))).collectList())
                            .onErrorMap(Exception.class, utils::handleException)
                            .log(LOG.getName(), Level.FINE);
                    });


        } catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed: {}", re.toString());
            throw re;
        }

    }

    private <T> T observationWithProductInfo(String productInfo, Supplier<T> supplier) {
        return observationUtil.observe(
                "composite observation",
                "product info",
                "productId",
                productInfo,
                supplier);
    }

}
