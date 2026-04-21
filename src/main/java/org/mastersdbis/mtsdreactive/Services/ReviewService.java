package org.mastersdbis.mtsdreactive.Services;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.Entities.Review;
import org.mastersdbis.mtsdreactive.Repositories.ProviderRepository;
import org.mastersdbis.mtsdreactive.Repositories.ReviewRepository;
import org.mastersdbis.mtsdreactive.Repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;

    // ----------------------------------------------------------------
    // Queries
    // ----------------------------------------------------------------

    public Mono<Review> findById(Integer id) {
        return reviewRepository.findById(id);
    }

    public Flux<Review> findByServiceId(Integer serviceId) {
        return reviewRepository.findByServiceId(serviceId);
    }

    public Flux<Review> findByPoster(Integer userId) {
        return reviewRepository.findByUserThatLeftTheReviewId(userId);
    }

    public Flux<Review> findByUserReviewed(Integer userId) {
        return reviewRepository.findByUserReviewedId(userId);
    }

    // ----------------------------------------------------------------
    // Mutations
    // ----------------------------------------------------------------

    @Transactional
    public Mono<Review> saveReview(Review review) {
        // Determine review type by checking if the reviewed user is a provider
        return providerRepository.findById(review.getUserReviewedId())
                .hasElement()
                .flatMap(isProvider -> {
                    review.setReviewType(
                            isProvider ? "CUSTOMER_TO_PROVIDER" : "PROVIDER_TO_CLIENT");

                    // Validate: PROVIDER_TO_CLIENT must have null quality and price
                    if (!isProvider) {
                        review.setQuality(null);
                        review.setPrice(null);
                    }

                    double overall = calculateOverall(review, isProvider);
                    review.setOverallSatisfaction(overall);
                    review.setDateCreated(LocalDateTime.now());
                    review.setDateUpdated(LocalDateTime.now());

                    return reviewRepository.save(review);
                })
                .flatMap(saved -> recalculateUserRating(saved.getUserReviewedId())
                        .thenReturn(saved));
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private double calculateOverall(Review review, boolean isProvider) {
        double prof   = orZero(review.getProfessionalism());
        double prompt = orZero(review.getPromptitude());
        double comm   = orZero(review.getCommunication());

        if (isProvider) {
            double quality = orZero(review.getQuality());
            double price   = orZero(review.getPrice());
            return Math.round(((prof + prompt + comm + quality + price) / 5.0) * 100.0) / 100.0;
        } else {
            return Math.round(((prof + prompt + comm) / 3.0) * 100.0) / 100.0;
        }
    }

    private double orZero(Integer value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    /**
     * Recalculates and persists the aggregate rating for a user
     * after a new review is saved.
     */
    private Mono<Void> recalculateUserRating(Integer userId) {
        return Mono.zip(
                        reviewRepository.sumOverallSatisfactionByUserReviewedId(userId)
                                .defaultIfEmpty(0.0),
                        reviewRepository.countByUserReviewedId(userId)
                                .defaultIfEmpty(0)
                )
                .flatMap(tuple -> {
                    double sum   = tuple.getT1();
                    int    count = tuple.getT2();
                    double avg   = count == 0 ? 0.0 :
                            Math.round((sum / count) * 100.0) / 100.0;
                    return userRepository.updateUserRating(userId, avg);
                });
    }
}