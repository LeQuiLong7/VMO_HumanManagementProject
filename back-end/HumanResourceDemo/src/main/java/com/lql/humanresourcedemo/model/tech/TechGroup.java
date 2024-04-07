package com.lql.humanresourcedemo.model.tech;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class TechGroup {
    @Id
    @SequenceGenerator(name = "tech_id_seq", sequenceName = "tech_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tech_id_seq")
    private Long id;
    private String name;
}
