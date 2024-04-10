package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.tech.Tech;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechRepository extends JpaRepository<Tech, Long> {
}
