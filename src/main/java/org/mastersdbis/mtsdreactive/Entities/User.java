package org.mastersdbis.mtsdreactive.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * R2DBC entity — no JPA annotations, no relationships declared here.
 * Roles are loaded separately via UserRoleRepository and stored in
 * the @Transient field (not persisted by R2DBC).
 */
@Table("users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column("id")
    private Integer id;

    @Column("date_created")
    private LocalDateTime dateCreated;

    @Column("date_updated")
    private LocalDateTime dateUpdated;

    @Column("address")
    private String address;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("phonenumber")
    private String phoneNumber;

    @Column("rating")
    private Double rating;

    @Column("username")
    private String username;

    // Not in the users table — loaded reactively from user_roles
    @Transient
    private Set<String> roles = new HashSet<>();
}
