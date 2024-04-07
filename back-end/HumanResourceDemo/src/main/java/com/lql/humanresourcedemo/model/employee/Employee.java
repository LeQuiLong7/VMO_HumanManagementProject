package com.lql.humanresourcedemo.model.employee;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Employee extends Auditable {
    @Id
    @SequenceGenerator(name = "employee_id_seq", sequenceName = "employee_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_seq")
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String avatarUrl;
    private String personalEmail;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne
    @JoinColumn(name = "manageBy")
    private Employee managedBy;


}
