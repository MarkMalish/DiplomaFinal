package com.example.eventmanager.repository;

import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByOrganizer(User organizer);
    List<Event> findByCategoryId(Long categoryId);

    List<Event> findByTitleContainingIgnoreCase(String keyword);

    List<Event> findByCategoryIdAndTitleContainingIgnoreCase(Long categoryId, String keyword);
    Page<Event> findAll(Pageable pageable);
    Page<Event> findByCategoryIdAndTitleContainingIgnoreCase(Long categoryId, String title, Pageable pageable);
    Page<Event> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<Event> findByArchivedTrue();
    Page<Event> findByArchivedTrue(Pageable pageable);



}
