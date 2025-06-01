package com.example.eventmanager.repository;

import com.example.eventmanager.model.Category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
