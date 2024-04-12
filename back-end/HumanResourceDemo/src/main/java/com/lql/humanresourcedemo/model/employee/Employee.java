package com.lql.humanresourcedemo.model.employee;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIdentityReference(alwaysAsId = true)
public class Employee extends Auditable {
    @Id
    @SequenceGenerator(name = "employee_id_seq", sequenceName = "employee_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_seq")
    private Long id;

    @Column(updatable = false)
    private String email;

    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String avatarUrl;
    private String personalEmail;
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean quit;
    @Column(columnDefinition = "SMALLINT DEFAULT 0")
    private Byte leaveDays;
    private Double currentSalary;
    private LocalDateTime lastUpdatedAt;

    @PreUpdate
    private void preUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manageBy")
    private Employee managedBy;


}
