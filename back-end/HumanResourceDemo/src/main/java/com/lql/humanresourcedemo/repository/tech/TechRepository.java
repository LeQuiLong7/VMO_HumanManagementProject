package com.lql.humanresourcedemo.repository.tech;

import com.lql.humanresourcedemo.model.tech.Tech;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechRepository extends JpaRepository<Tech, Long> {
}
