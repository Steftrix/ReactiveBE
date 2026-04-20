package com.mastersdbis.mtsdreactive.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * R2DBC has no @Embedded support.
 * The Rating embeddable from the imperative app is flattened
 * into direct columns here — matching the actual DB schema.
 */
@Table("review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @Column("id")
    private Integer id;

    @Column("date_created")
    private LocalDateTime dateCreated;

    @Column("date_updated")
    private LocalDateTime dateUpdated;

    @Column("content")
    private String content;

    // Flattened rating fields
    @Column("communication")
    private Integer communication;

    @Column("overall_satisfaction")
    private Double overallSatisfaction;

    @Column("price")
    private Integer price;

    @Column("professionalism")
    private Integer professionalism;

    @Column("promptitude")
    private Integer promptitude;

    @Column("quality")
    private Integer quality;

    @Column("review_type")
    private String reviewType;

    @Column("service_id")
    private Integer serviceId;

    @Column("user_reviewed")
    private Integer userReviewedId;

    @Column("user_that_left_the_review")
    private Integer userThatLeftTheReviewId;
}
