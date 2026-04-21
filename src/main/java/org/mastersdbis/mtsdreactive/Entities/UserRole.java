package org.mastersdbis.mtsdreactive.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Maps the user_roles join table.
 * No @Id — this table has a composite key (user_id, roles).
 * Operations are done via custom queries.
 */
@Table("user_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @Column("user_id")
    private Integer userId;

    @Column("roles")
    private String role;  // stores enum name: CLIENT, PROVIDER, ADMIN
}