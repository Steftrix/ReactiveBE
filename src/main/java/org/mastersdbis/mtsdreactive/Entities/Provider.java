package org.mastersdbis.mtsdreactive.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Maps the provider table.
 * servicedomain stored as smallint (Java enum ordinal) — use Integer here.
 * approved_by is a FK to users.id — stored as Integer.
 * id shares the value with users.id (@MapsId in JPA — in R2DBC we just set it manually).
 */
@Table("provider")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Provider {

    @Id
    @Column("id")
    private Integer id;  // same value as user.id

    @Column("date_created")
    private LocalDateTime dateCreated;

    @Column("date_updated")
    private LocalDateTime dateUpdated;

    @Column("bankiban")
    private String bankIBAN;

    @Column("cif")
    private String cif;

    @Column("companyadress")
    private String companyAdress;

    @Column("companyname")
    private String companyName;

    @Column("servicedomain")
    private Integer serviceDomain;  // smallint ordinal

    @Column("validationstatus")
    private String validationStatus;

    @Column("approved_by")
    private Integer approvedById;
}