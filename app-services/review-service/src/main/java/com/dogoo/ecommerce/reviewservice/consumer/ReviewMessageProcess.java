package com.dogoo.ecommerce.reviewservice.consumer;

import com.dogoo.common.lib.Event;
import com.dogoo.ecommerce.review.api.model.Review;
import com.dogoo.ecommerce.reviewservice.service.ReviewService;
import com.dogoo.exception.model.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class ReviewMessageProcess {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewMessageProcess.class);

    private final ReviewService service;

    public ReviewMessageProcess(ReviewService service) {
        this.service = service;
    }

    @Bean
    public Consumer<Event<String, Review>> messageProcessor() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {

                case CREATE:
                    Review review = event.getData();
                    LOG.info("Create product with ID: {}", review.getId());
                    service.addReview(review).block();
                    break;

                case DELETE:
                    String productId = event.getKey();
                    LOG.info("Delete all reviews release to product with ProductID: {}", productId);
                    service.deleteByProductId(productId).block();
                    break;

                case UPDATE:
                    String reviewId = event.getKey();
                    review = event.getData();
                    LOG.info("Update review with reviewId: {}", reviewId);
                    service.saveReview(reviewId, review).block();
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or UPDATE or DELETE event";
                    LOG.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }

            LOG.info("Message processing done!");

        };
    }
}
