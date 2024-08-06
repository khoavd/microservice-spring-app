package com.dogoo.ecommerce.reviewservice.mapper;

import com.dogoo.ecommerce.review.api.model.Review;
import com.dogoo.ecommerce.reviewservice.entry.ReviewEntry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

    public Review mapModelFromEntry(ReviewEntry from) {
        Review to = new Review();

        to.setId(from.getId().toString());
        to.setProductId(from.getProductId());

        to.setAuthor(from.getAuthor());
        to.setContent(from.getContent());
        to.setSubject(from.getSubject());

        return to;
    }

    public List<Review> mapModelsFromEntries(List<ReviewEntry> from) {
        return from.stream().map(e -> mapModelFromEntry(e)).collect(Collectors.toList());
    }

    public ReviewEntry mapFromModelToEntry(Review from) {
        ReviewEntry to = new ReviewEntry();

        to.setId(UUID.randomUUID());
        to.setAuthor(from.getAuthor());
        to.setContent(from.getContent());
        to.setSubject(from.getSubject());
        to.setProductId(from.getProductId());

        return to;
    }


    public ReviewEntry mapFromModelToEntry(ReviewEntry to, Review from) {
        
        to.setUpdatedDate(LocalDateTime.now());

        to.setAuthor(from.getAuthor());
        to.setContent(from.getContent());
        to.setSubject(from.getSubject());

        return to;
    }
}
