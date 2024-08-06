package com.dogoo.ecommerce.recommendationservice.consumer;

import com.dogoo.common.lib.Event;
import com.dogoo.ecommerce.recommendation.api.model.Recommendation;
import com.dogoo.ecommerce.recommendationservice.service.RecommendationService;
import com.dogoo.exception.model.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class RecomendationMessageProcess {
    
    private static final Logger LOG = LoggerFactory.getLogger(RecomendationMessageProcess.class);

    private final RecommendationService service;

    public RecomendationMessageProcess(RecommendationService service) {
        this.service = service;
    }

    @Bean
    public Consumer<Event<String, Recommendation>> messageProcessor() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {

                case CREATE:
                    Recommendation rcmd = event.getData();
                    LOG.info("Create rcmd with ID: {}", rcmd.getId());
                    service.addRecomendation(rcmd).block();
                    break;

                case DELETE:
                    String productId = event.getKey();
                    LOG.info("Delete rcmd with product ID: {}", productId);
                    service.deleteByProductId(productId).block();
                    break;

                case UPDATE:
                    String rcmdId = event.getKey();
                    rcmd = event.getData();
                    LOG.info("Update rcmd with Id: {}", rcmdId);
                    service.updateRecomendation(rcmdId, rcmd).block();
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                    LOG.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }

            LOG.info("Message processing done!");

        };
    }
}
