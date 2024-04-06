package com.lql.humanresourcedemo.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.io.Serializable;

@Entity
public class EmployeeTech {

    @EmbeddedId
    private EmployeeTechId id;
    private Double yearOfExperience;

    @Embeddable
    public static class EmployeeTechId implements Serializable {
        private Employee employee;
        private Tech tech;
    }
}

