package com.lql.humanresourcedemo.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class PasswordResetRequest {
    @EmbeddedId
    private PasswordResetRequestId id;
    private LocalDateTime validUntil;

    @Embeddable
    public static class PasswordResetRequestId implements Serializable {
        private Employee employee;
        private String token;
    }

}
