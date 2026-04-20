package com.diz.rea.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("provider")
@Getter
@Setter
@NoArgsConstructor
public class Provider extends AbstractEntity {

    @Id
    private Integer id;

    // No @OneToOne — user loaded separately
    @Column("id")
    private Integer userId;  // foreign key only

    @Column("cif")
    private String cif;

    @Column("companyname")
    private String companyName;

    @Column("companyadress")
    private String companyAdress;

    @Column("servicedomain")
    private String serviceDomain;

    @Column("bankiban")
    private String bankIBAN;

    @Column("validationstatus")
    private String validationStatus;

    @Column("approved_by")
    private Integer approvedById;
}