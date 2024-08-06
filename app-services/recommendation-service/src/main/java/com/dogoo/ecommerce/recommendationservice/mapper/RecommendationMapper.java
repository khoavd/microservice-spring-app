package com.dogoo.ecommerce.recommendationservice.mapper;

import com.dogoo.ecommerce.recommendation.api.model.Recommendation;
import com.dogoo.ecommerce.recommendationservice.entry.RecommendationEntry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RecommendationMapper {

    public Recommendation mapModelFromEntry(RecommendationEntry from) {
        Recommendation to = new Recommendation();

        to.setId(from.getId().toString());
        to.setProductId(from.getProductId());
        to.setAuthor(from.getAuthor());
        to.setContent(from.getContent());
        to.setRate(from.getRate());

        return to;
    }

    public List<Recommendation> mapModelsFromEntries(List<RecommendationEntry> from) {
        return from.stream().map(e -> mapModelFromEntry(e)).collect(Collectors.toList());
    }

    public RecommendationEntry mapEntryFromModel(Recommendation from) {
        RecommendationEntry to = new RecommendationEntry();

        to.setId(UUID.randomUUID());

        to.setProductId(from.getProductId());
        to.setAuthor(from.getAuthor());
        to.setContent(from.getContent());
        to.setRate(from.getRate());

        return to;
    }

    public RecommendationEntry mapEntryFromModel(RecommendationEntry to,
                                                 Recommendation from) {
        
        to.setUpdatedDate(LocalDateTime.now());

        to.setContent(from.getContent());
        to.setRate(from.getRate());

        return to;
    }
}
