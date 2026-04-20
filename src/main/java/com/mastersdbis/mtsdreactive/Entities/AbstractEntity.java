package com.mastersdbis.mtsdreactive.Entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class AbstractEntity {
    @Column("date_created")
    private LocalDateTime dateCreated = LocalDateTime.now();

    @Column("date_updated")
    private LocalDateTime dateUpdated = LocalDateTime.now();
}
