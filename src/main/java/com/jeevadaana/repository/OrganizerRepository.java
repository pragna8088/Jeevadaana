package com.jeevadaana.repository;

import com.jeevadaana.model.Organizer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

    Optional<Organizer> findByEmail(String email);

    boolean existsByEmail(String email);
}
