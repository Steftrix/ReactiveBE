package com.diz.rea.Services;

import com.diz.rea.Entities.Review;
import com.diz.rea.Repositories.ProviderRepository;
import com.diz.rea.Repositories.ReviewRepository;
import com.diz.rea.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;

    public Mono<Review> saveReview(Review review) {
        return providerRepository.findByUserId(review.getUserReviewedId())
                .hasElement()
                .flatMap(isProvider -> {
                    review.setReviewType(isProvider ?
                            "CUSTOMER_TO_PROVIDER" : "PROVIDER_TO_CLIENT");
                    double avg = calculateAverage(review, isProvider);
                    review.setOverallSatisfaction(avg);
                    review.setDateCreated(LocalDateTime.now());
                    review.setDateUpdated(LocalDateTime.now());
                    return reviewRepository.save(review);
                })
                .flatMap(saved ->
                        updateUserRating(review.getUserReviewedId())
                                .thenReturn(saved));
    }

    private Mono<Void> updateUserRating(Integer userId) {
        return Mono.zip(
                reviewRepository.sumOfReviewsByUserReviewedId(userId),
                reviewRepository.countReviewsByUserReviewedId(userId)
        ).flatMap(tuple -> {
            double avg = tuple.getT2() == 0 ? 0 :
                    tuple.getT1() / tuple.getT2();
            return userRepository.findById(userId)
                    .flatMap(user -> {
                        user.setRating(avg);
                        return userRepository.save(user);
                    });
        }).then();
    }
}
