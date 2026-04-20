package com.diz.rea.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("review")
@Getter
@Setter
@NoArgsConstructor
public class Review extends AbstractEntity {

    @Id
    private Integer id;

    @Column("user_that_left_the_review")
    private Integer userThatLeftTheReviewId;

    @Column("user_reviewed")
    private Integer userReviewedId;

    @Column("service_id")
    private Integer serviceId;

    @Column("professionalism")
    private Integer professionalism;

    @Column("promptitude")
    private Integer promptitude;

    @Column("quality")
    private Integer quality;

    @Column("communication")
    private Integer communication;

    @Column("price")
    private Integer price;

    @Column("overall_satisfaction")
    private Double overallSatisfaction;

    @Column("content")
    private String content;

    @Column("review_type")
    private String reviewType;
}
