package org.mastersdbis.mtsdreactive.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service {

    @Id
    @Column("id")
    private Integer id;

    @Column("date_created")
    private LocalDateTime dateCreated;

    @Column("date_updated")
    private LocalDateTime dateUpdated;

    @Column("accepted_payment_methods")
    private String acceptedPaymentMethods;

    @Column("active")
    private Boolean active;

    @Column("description")
    private String description;

    @Column("domain")
    private String domain;

    @Column("minimum_booking_time")
    private Integer minimumBookingTime;

    @Column("name")
    private String name;

    @Column("price")
    private Double price;

    @Column("region")
    private String region;

    @Column("servicetype")
    private String serviceType;

    @Column("subdomain")
    private String subdomain;

    @Column("provider_id")
    private Integer providerId;
}
