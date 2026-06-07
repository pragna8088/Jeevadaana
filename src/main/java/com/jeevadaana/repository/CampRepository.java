package com.jeevadaana.repository;

import com.jeevadaana.model.Camp;
import com.jeevadaana.model.CampStatus;
import com.jeevadaana.model.Organizer;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CampRepository extends JpaRepository<Camp, Long> {

    List<Camp> findByOrganizerOrderByCampDateDesc(Organizer organizer);

    List<Camp> findByStatusOrderByCampDateAsc(CampStatus status);

    List<Camp> findByDistrictIgnoreCaseAndStatusOrderByCampDateAsc(String district, CampStatus status);

    @Query("SELECT c FROM Camp c WHERE c.status = :status "
            + "AND (:district IS NULL OR :district = '' OR LOWER(c.district) LIKE LOWER(CONCAT('%', :district, '%'))) "
            + "ORDER BY c.campDate ASC")
    List<Camp> searchUpcoming(@Param("district") String district, @Param("status") CampStatus status);

    List<Camp> findByCampDateGreaterThanEqualAndStatusOrderByCampDateAsc(LocalDate date, CampStatus status);

    @Query("SELECT DISTINCT c.district FROM Camp c ORDER BY c.district ASC")
    List<String> findDistinctDistricts();
}
