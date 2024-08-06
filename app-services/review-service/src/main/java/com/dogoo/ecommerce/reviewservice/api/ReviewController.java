package com.dogoo.ecommerce.reviewservice.api;


import com.dogoo.ecommerce.review.api.model.Review;
import com.dogoo.ecommerce.reviewservice.service.ReviewService;
import com.dogoo.ecommerce.revirew.api.ReviewsApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class ReviewController implements ReviewsApi {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @Override
    public Mono<ResponseEntity<Flux<Review>>> getReviews(String productId, 
                                                         String filter, 
                                                         Integer page, 
                                                         Integer pageSize, 
                                                         String search, 
                                                         String sort, 
                                                         ServerWebExchange exchange) {
        return Mono.just(
                new ResponseEntity<>(
                        service.getReviewByProductId(productId),
                        HttpStatus.OK));    
    }
}
