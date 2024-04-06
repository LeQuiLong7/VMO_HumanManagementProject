package com.lql.humanresourcedemo.model;

import jakarta.persistence.*;

@Entity
public class TechGroup {
    @Id
    @SequenceGenerator(name = "tech_id_seq", sequenceName = "tech_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tech_id_seq")
    private Long id;
    private String name;
}
