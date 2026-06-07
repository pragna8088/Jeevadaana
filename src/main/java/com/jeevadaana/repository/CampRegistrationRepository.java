package com.jeevadaana.repository;

import com.jeevadaana.model.Camp;
import com.jeevadaana.model.CampRegistration;
import com.jeevadaana.model.Donor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampRegistrationRepository extends JpaRepository<CampRegistration, Long> {

    List<CampRegistration> findByDonorOrderByRegisteredAtDesc(Donor donor);

    List<CampRegistration> findByCampOrderByRegisteredAtAsc(Camp camp);

    Optional<CampRegistration> findByCampAndDonor(Camp camp, Donor donor);

    boolean existsByCampAndDonor(Camp camp, Donor donor);

    long countByCamp(Camp camp);
}
