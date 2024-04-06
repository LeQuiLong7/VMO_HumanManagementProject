package com.lql.humanresourcedemo.model;

import com.lql.humanresourcedemo.enumeration.Role;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Employee extends Auditable{


    @Id
    @SequenceGenerator(name = "employee_id_seq", sequenceName = "employee_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_seq")
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String avatarUrl;
    private String personalEmail;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne
    private Employee managedBy;


}
