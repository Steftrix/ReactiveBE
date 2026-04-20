package com.diz.rea.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractEntity {

    @Id
    private Integer id;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("email")
    private String email;

    @Column("phonenumber")
    private String phoneNumber;

    @Column("address")
    private String address;

    @Column("rating")
    private Double rating;

    // Roles stored in separate table — not mapped here
    // Loaded separately via UserRoleRepository
}
