package com.dogoo.ecommerce.recommendationservice.repository;

import com.dogoo.ecommerce.recommendationservice.entry.RecommendationEntry;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RecommendationRepository extends ReactiveCrudRepository<RecommendationEntry, UUID> {
    Flux<RecommendationEntry> getAllByProductId(String productId);

}
