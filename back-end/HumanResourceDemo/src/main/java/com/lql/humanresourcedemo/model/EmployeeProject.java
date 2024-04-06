package com.lql.humanresourcedemo.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import java.io.Serializable;

@Entity
public class EmployeeProject extends Auditable {

    @EmbeddedId
    private EmployeeProjectId id;

    @Embeddable
    public static class EmployeeProjectId implements Serializable {
        private Employee employee;
        private Project project;
    }
}
