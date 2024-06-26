package com.lql.humanresourcedemo.model.tech;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Tech {
    @Id
    @SequenceGenerator(name = "tech_id_seq", sequenceName = "tech_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tech_id_seq")
    private Long id;
    private String name;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "techGroupId")
    private TechGroup techGroup;
}
