package com.lql.humanresourcedemo.model.client;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Client {
    @Id
    @SequenceGenerator(name = "client_id_seq", sequenceName = "client_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_seq")
    private Long id;
    private String companyName;
    private String country;
    private String description;

    private String logoUrl;
}
