package com.diz.rea.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("service")
@Getter
@Setter
@NoArgsConstructor
public class Service extends AbstractEntity {

    @Id
    private Integer id;

    @Column("provider_id")
    private Integer providerId;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("domain")
    private String domain;

    @Column("subdomain")
    private String subdomain;

    @Column("price")
    private Double price;

    @Column("region")
    private String region;

    @Column("active")
    private Boolean active;

    @Column("accepted_payment_methods")
    private String acceptedPaymentMethods;

    @Column("servicetype")
    private String serviceType;

    @Column("minimum_booking_time")
    private Integer minimumBookingTime;
}
