package com.lql.humanresourcedemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class Auditable {

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @Column(updatable = false, nullable = false)
    private Long createdBy;

    @PrePersist
    public void setup() {
        this.createdAt = LocalDateTime.now();
        // TODO: get the id from the authenticated user in security context
        this.createdBy = 1L;
    }
}
