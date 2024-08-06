package com.dogoo.ecommerce.productcompositeservice.producer;

import com.dogoo.common.lib.Event;
import com.dogoo.ecommerce.product.client.model.Product;
import com.dogoo.ecommerce.recommendation.client.model.Recommendation;
import com.dogoo.ecommerce.review.client.model.Review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
public class ProductCompositeProducerService {

    private final Logger LOG = LoggerFactory.getLogger(ProductCompositeProducerService.class);

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    public ProductCompositeProducerService(StreamBridge streamBridge,
                                           @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    public Mono<Product> createProduct(Product body) {

        return Mono.fromCallable(() -> {
            sendMessage("products-out-0", new Event(Event.Type.CREATE, body.getId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    public Mono<Review> createReview(Review body) {

        return Mono.fromCallable(() -> {
            sendMessage("reviews-out-0", new Event(Event.Type.CREATE, body.getId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    public Mono<Void> deleteProduct(String productId) {
        return  Mono.fromRunnable(() ->
                sendMessage(
                        "products-out-0",
                        new Event(Event.Type.DELETE, productId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    public Mono<Product> updateProduct(String productId, Product body) {
        return Mono.fromCallable(() -> {
            sendMessage("products-out-0", new Event(Event.Type.UPDATE, productId, body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    public Mono<Review> updateReview(Review body) {
        return Mono.fromCallable(() -> {
            sendMessage("reviews-out-0", new Event(Event.Type.UPDATE, body.getId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    public Mono<Void> deleteReview(String productId) {
        return  Mono.fromRunnable(() ->
                sendMessage(
                        "reviews-out-0",
                        new Event(Event.Type.DELETE, productId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    public Mono<Recommendation> createRcmd(Recommendation body) {

        return Mono.fromCallable(() -> {
            sendMessage("recommendations-out-0", new Event(Event.Type.CREATE, body.getId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    public Mono<Void> deleteRcmd(String productId) {
        return  Mono.fromRunnable(() ->
                sendMessage(
                        "recommendations-out-0",
                        new Event(Event.Type.DELETE, productId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    public Mono<Recommendation> updateRcmd(Recommendation body) {

        return Mono.fromCallable(() -> {
            sendMessage("recommendations-out-0", new Event(Event.Type.UPDATE, body.getId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    private void sendMessage(String bindingName, Event event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(bindingName, message);
    }
}
