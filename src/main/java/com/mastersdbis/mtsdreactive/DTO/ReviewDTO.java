package com.mastersdbis.mtsdreactive.DTO;

import com.mastersdbis.mtsdreactive.Entities.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    private Integer id;

    @NotNull
    private Integer userThatLeftTheReviewId;

    @NotNull
    private Integer userReviewedId;

    @NotNull
    private Integer serviceId;

    @Min(1) @Max(5)
    private Integer professionalism;

    @Min(1) @Max(5)
    private Integer promptitude;

    @Min(1) @Max(5)
    private Integer quality;        // nullable for PROVIDER_TO_CLIENT

    @Min(1) @Max(5)
    private Integer communication;

    @Min(1) @Max(5)
    private Integer price;          // nullable for PROVIDER_TO_CLIENT

    private Double overallSatisfaction;

    @Size(max = 500)
    private String content;

    @NotNull
    private String reviewType;

    public static ReviewDTO fromReview(Review r) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(r.getId());
        dto.setUserThatLeftTheReviewId(r.getUserThatLeftTheReviewId());
        dto.setUserReviewedId(r.getUserReviewedId());
        dto.setServiceId(r.getServiceId());
        dto.setProfessionalism(r.getProfessionalism());
        dto.setPromptitude(r.getPromptitude());
        dto.setQuality(r.getQuality());
        dto.setCommunication(r.getCommunication());
        dto.setPrice(r.getPrice());
        dto.setOverallSatisfaction(r.getOverallSatisfaction());
        dto.setContent(r.getContent());
        dto.setReviewType(r.getReviewType());
        return dto;
    }
}