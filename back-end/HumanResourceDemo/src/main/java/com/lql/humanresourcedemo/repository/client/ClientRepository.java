package com.lql.humanresourcedemo.repository.client;

import com.lql.humanresourcedemo.model.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
