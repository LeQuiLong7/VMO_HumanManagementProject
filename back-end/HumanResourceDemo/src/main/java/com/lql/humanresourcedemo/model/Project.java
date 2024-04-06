package com.lql.humanresourcedemo.model;

import com.lql.humanresourcedemo.enumeration.ProjectState;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Project extends Auditable{
    @Id
    @SequenceGenerator(name = "project_id_seq", sequenceName = "project_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_id_seq")
    private Long id;
    private String name;
    private String description;
    private ProjectState state;
    private LocalDate startDate;
    private LocalDate finishDate;

    @OneToOne
    @JoinColumn(name = "clientId")
    private Client client;
}
