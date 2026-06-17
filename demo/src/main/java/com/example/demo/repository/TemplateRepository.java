package com.example.demo.repository;

import com.example.demo.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    Optional<Template> findByName(String name);

    List<Template> findByActive(boolean active);

    boolean existsByName(String name);
}