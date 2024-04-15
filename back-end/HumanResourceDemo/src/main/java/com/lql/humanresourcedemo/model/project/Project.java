package com.lql.humanresourcedemo.model.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lql.humanresourcedemo.enumeration.ProjectState;
import com.lql.humanresourcedemo.model.Auditable;
import com.lql.humanresourcedemo.model.client.Client;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project extends Auditable {
    @Id
    @SequenceGenerator(name = "project_id_seq", sequenceName = "project_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_id_seq")
    private Long id;
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectState state;

    private LocalDate expectedStartDate;
    private LocalDate expectedFinishDate;

    private LocalDate actualStartDate;
    private LocalDate actualFinishDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientId")
    private Client client;
}
