package com.jeevadaana.repository;

import com.jeevadaana.model.Donor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    Optional<Donor> findByEmail(String email);

    boolean existsByEmail(String email);
}
