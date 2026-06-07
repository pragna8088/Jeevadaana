package com.jeevadaana.service;

import com.jeevadaana.model.Camp;
import com.jeevadaana.model.CampRegistration;
import com.jeevadaana.model.Donation;
import com.jeevadaana.model.Donor;
import com.jeevadaana.model.RegistrationStatus;
import com.jeevadaana.repository.DonationRepository;
import com.jeevadaana.repository.CampRegistrationRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final CampRegistrationRepository registrationRepository;

    public DonationService(DonationRepository donationRepository,
                           CampRegistrationRepository registrationRepository) {
        this.donationRepository = donationRepository;
        this.registrationRepository = registrationRepository;
    }

    /**
     * Records a donation for a registered donor as part of post-camp management
     * and marks the registration as ATTENDED.
     */
    @Transactional
    public Donation recordFromRegistration(CampRegistration registration, Integer unitsMl, String remarks) {
        Donor donor = registration.getDonor();
        Camp camp = registration.getCamp();

        Donation donation = new Donation();
        donation.setDonor(donor);
        donation.setCamp(camp);
        donation.setBloodGroup(donor.getBloodGroup());
        donation.setUnitsMl(unitsMl != null ? unitsMl : 450);
        donation.setDonationDate(camp.getCampDate() != null ? camp.getCampDate() : LocalDate.now());
        donation.setRemarks(remarks);

        registration.setStatus(RegistrationStatus.ATTENDED);
        registrationRepository.save(registration);

        return donationRepository.save(donation);
    }

    public List<Donation> historyForDonor(Donor donor) {
        return donationRepository.findByDonorOrderByDonationDateDesc(donor);
    }

    public List<Donation> listForCamp(Camp camp) {
        return donationRepository.findByCampOrderByDonationDateDesc(camp);
    }

    public long countForDonor(Donor donor) {
        return donationRepository.countByDonor(donor);
    }
}
