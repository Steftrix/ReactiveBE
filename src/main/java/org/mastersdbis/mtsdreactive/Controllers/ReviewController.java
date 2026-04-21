package org.mastersdbis.mtsdreactive.Controllers;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.DTO.ReviewDTO;
import org.mastersdbis.mtsdreactive.Entities.Review;
import org.mastersdbis.mtsdreactive.Services.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Mono<ResponseEntity<String>> saveReview(@RequestBody ReviewDTO dto) {
        Review review = new Review();
        review.setUserThatLeftTheReviewId(dto.getUserThatLeftTheReviewId());
        review.setUserReviewedId(dto.getUserReviewedId());
        review.setServiceId(dto.getServiceId());
        review.setProfessionalism(dto.getProfessionalism());
        review.setPromptitude(dto.getPromptitude());
        review.setQuality(dto.getQuality());
        review.setCommunication(dto.getCommunication());
        review.setPrice(dto.getPrice());
        review.setContent(dto.getContent());

        return reviewService.saveReview(review)
            .map(r -> ResponseEntity.ok("Review saved successfully."))
            .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ReviewDTO>> findById(@PathVariable Integer id) {
        return reviewService.findById(id)
            .map(r -> ResponseEntity.ok(ReviewDTO.fromReview(r)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/service/{serviceId}")
    public Mono<ResponseEntity<List<ReviewDTO>>> findByService(@PathVariable Integer serviceId) {
        return reviewService.findByServiceId(serviceId)
            .map(ReviewDTO::fromReview)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/poster/{userId}")
    public Mono<ResponseEntity<List<ReviewDTO>>> findByPoster(@PathVariable Integer userId) {
        return reviewService.findByPoster(userId)
            .map(ReviewDTO::fromReview)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/reviewed/{userId}")
    public Mono<ResponseEntity<List<ReviewDTO>>> findByReviewed(@PathVariable Integer userId) {
        return reviewService.findByUserReviewed(userId)
            .map(ReviewDTO::fromReview)
            .collectList()
            .map(ResponseEntity::ok);
    }
}