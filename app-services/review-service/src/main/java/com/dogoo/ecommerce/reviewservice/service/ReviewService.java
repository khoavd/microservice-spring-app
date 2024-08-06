package com.dogoo.ecommerce.reviewservice.service;

import com.dogoo.ecommerce.review.api.model.Review;
import com.dogoo.ecommerce.reviewservice.entry.ReviewEntry;
import com.dogoo.ecommerce.reviewservice.mapper.ReviewMapper;
import com.dogoo.ecommerce.reviewservice.repository.ReviewRepository;
import com.dogoo.exception.model.InternalServerException;
import com.dogoo.exception.model.InvalidInputException;
import com.dogoo.exception.model.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;
import java.util.logging.Level;

@Service
public class ReviewService {

    private final Logger LOG = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository repo;

    private final ReviewMapper mapper;

    public ReviewService(ReviewRepository repo, ReviewMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public Flux<Review> getReviewByProductId(String productId) {
        //return repo.getAllByProductId(productId).map(mapper::mapModelFromEntry).delayElements(Duration.ofSeconds(2));
        return repo.getAllByProductId(productId).map(mapper::mapModelFromEntry);
    }

    public Mono<Review> addReview(Review review) {
        return Mono.just(review)
            .map(mapper::mapFromModelToEntry)
            .flatMap(repo::save)
            .map(mapper::mapModelFromEntry)
            .log(LOG.getName(), Level.FINE)
            .onErrorMap(
                    DuplicateKeyException.class,
                    ex -> new InvalidInputException("Duplicate key, Review Id" + review.getId()))
            .onErrorMap(ex -> new InternalServerException("InternalServerException " + ex.getMessage()))
            .doOnError(ex -> LOG.error(ex.getMessage()));

    }

    public Mono<Review> saveReview(String reviewId, Review review) {

        return Mono.just(reviewId)
            .map(UUID::fromString)
            .onErrorResume(ex -> Mono.error(new NotFoundException("UUID_INVALID")))
            .flatMap(repo::findById)
            .switchIfEmpty(Mono.error(new NotFoundException("REVIEW_NOT_FOUND")))
            .map(e -> mapper.mapFromModelToEntry(e, review))
            .flatMap(repo::save)
            .map(mapper::mapModelFromEntry)
            .log(LOG.getName(), Level.FINE)
            .onErrorMap(
                    DuplicateKeyException.class,
                    ex -> new InvalidInputException("Duplicate key, review Id" + reviewId))
            .onErrorMap(ex -> new InternalServerException("InternalServerException " + ex.getMessage()))
            .doOnError(ex -> LOG.error(ex.getMessage()));

    }

    private Mono<Void> deleteById(String reviewId) {

        return Mono.just(reviewId)
            .map(UUID::fromString)
            .onErrorResume(ex -> Mono.error(new NotFoundException("UUID_INVALID")))
            .flatMap(repo::deleteById)
            .switchIfEmpty(Mono.error(new NotFoundException("PRODUCT_NOT_FOUND")))
            .log(LOG.getName(), Level.FINE);
    }

    public Mono<Void> deleteByProductId(String productId) {

        return repo.deleteAll(repo.getAllByProductId(productId));
    }
}
