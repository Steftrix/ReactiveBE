package com.mastersdbis.mtsdreactive.Repositories;

import com.mastersdbis.mtsdreactive.Entities.Review;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewRepository extends ReactiveCrudRepository<Review, Integer> {

    Flux<Review> findByServiceId(Integer serviceId);

    Flux<Review> findByUserReviewedId(Integer userReviewedId);

    Flux<Review> findByUserThatLeftTheReviewId(Integer userThatLeftTheReviewId);

    @Query("SELECT COALESCE(SUM(overall_satisfaction), 0) FROM review WHERE user_reviewed = :userId")
    Mono<Double> sumOverallSatisfactionByUserReviewedId(Integer userId);

    @Query("SELECT COALESCE(COUNT(*), 0) FROM review WHERE user_reviewed = :userId")
    Mono<Integer> countByUserReviewedId(Integer userId);
}