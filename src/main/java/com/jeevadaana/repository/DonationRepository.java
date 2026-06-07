package com.jeevadaana.repository;

import com.jeevadaana.model.Camp;
import com.jeevadaana.model.Donation;
import com.jeevadaana.model.Donor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByDonorOrderByDonationDateDesc(Donor donor);

    List<Donation> findByCampOrderByDonationDateDesc(Camp camp);

    long countByDonor(Donor donor);
}
