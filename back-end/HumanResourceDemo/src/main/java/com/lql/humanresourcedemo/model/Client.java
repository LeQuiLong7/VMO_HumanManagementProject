package com.lql.humanresourcedemo.model;

import jakarta.persistence.*;

@Entity
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
