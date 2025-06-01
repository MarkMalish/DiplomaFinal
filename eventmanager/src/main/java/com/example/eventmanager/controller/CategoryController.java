package com.example.eventmanager.controller;

import com.example.eventmanager.mapper.CategoryMapper;
import com.example.eventmanager.model.Category;
import com.example.eventmanager.payload.Dto.CategoryDto;
import com.example.eventmanager.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryRepository categoryRepository,
                              CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        var categories = categoryRepository.findAll();
        var dtos = categories.stream()
                .map(categoryMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto dto) {
        var category = categoryMapper.fromDto(dto);
        var saved = categoryRepository.save(category);
        return ResponseEntity.ok(categoryMapper.toDto(saved));
    }
}

