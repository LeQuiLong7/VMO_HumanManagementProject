package com.lql.humanresourcedemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.lql.humanresourcedemo.util.ContextUtil.getCurrentEmployeeId;


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
        this.createdBy = getCurrentEmployeeId();
    }
}
