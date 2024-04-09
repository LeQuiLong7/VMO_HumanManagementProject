package com.lql.humanresourcedemo.model.project;

import com.lql.humanresourcedemo.enumeration.ProjectState;
import com.lql.humanresourcedemo.model.Auditable;
import com.lql.humanresourcedemo.model.client.Client;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Project extends Auditable {
    @Id
    @SequenceGenerator(name = "project_id_seq", sequenceName = "project_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_id_seq")
    private Long id;
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectState state;
    private LocalDate startDate;
    private LocalDate finishDate;

    @ManyToOne
    @JoinColumn(name = "clientId")
    private Client client;
}
