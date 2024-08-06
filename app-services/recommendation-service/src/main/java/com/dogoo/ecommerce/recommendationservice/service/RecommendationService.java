package com.dogoo.ecommerce.recommendationservice.service;

import com.dogoo.ecommerce.recommendation.api.model.Recommendation;
import com.dogoo.ecommerce.recommendationservice.mapper.RecommendationMapper;
import com.dogoo.ecommerce.recommendationservice.repository.RecommendationRepository;
import com.dogoo.exception.model.InternalServerException;
import com.dogoo.exception.model.InvalidInputException;
import com.dogoo.exception.model.NotFoundException;

import java.util.UUID;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RecommendationService {

    private final Logger LOG = LoggerFactory.getLogger(RecommendationService.class);

    private final RecommendationRepository repo;

    private final RecommendationMapper mapper;

    public RecommendationService(RecommendationRepository repo,
                                 RecommendationMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public Flux<Recommendation> getRecommendationsByProductId(String productId) {

        return repo.getAllByProductId(productId).map(mapper::mapModelFromEntry);
    }

    public Mono<Void> deleteById(String recomendationId) {
        return Mono
                .just(recomendationId)
                .map(UUID::fromString)
                .onErrorResume(ex -> Mono.error(new NotFoundException("UUID_INVALID " + ex.getMessage())))
                .flatMap(repo::deleteById)
                .switchIfEmpty(Mono.error(new NotFoundException("PRODUCT_NOT_FOUND")))
                .log(LOG.getName(), Level.FINE);
    }

    public Mono<Recommendation> addRecomendation(Recommendation data) {

        return Mono.just(data)
                .map(mapper::mapEntryFromModel)
                .flatMap(repo::save)
                .map(mapper::mapModelFromEntry)
                .log(LOG.getName(), Level.FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Review Id" + data.getId()))
                .onErrorMap(ex -> new InternalServerException("InternalServerException " + ex.getMessage()))
                .doOnError(ex -> LOG.error(ex.getMessage()));
    }


    public Mono<Recommendation> updateRecomendation(String id, Recommendation data) {

        return Mono.just(id)
            .map(UUID::fromString)
            .onErrorResume(ex -> Mono.error(new NotFoundException("UUID_INVALID" + ex.getMessage())))
            .flatMap(repo::findById)
            .switchIfEmpty(Mono.error(new NotFoundException("REVIEW_NOT_FOUND")))
            .map(e -> mapper.mapEntryFromModel(e, data))
            .flatMap(repo::save)
            .map(mapper::mapModelFromEntry)
            .log(LOG.getName(), Level.FINE)
            .onErrorMap(ex -> new InternalServerException("InternalServerException " + ex.getMessage()))
            .doOnError(ex -> LOG.error(ex.getMessage()));

    }

    public Mono<Void> deleteByProductId(String productId) {

        return repo.deleteAll(repo.getAllByProductId(productId));

    }
}
