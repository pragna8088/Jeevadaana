package com.jeevadaana.service;

import com.jeevadaana.model.Camp;
import com.jeevadaana.model.CampRegistration;
import com.jeevadaana.model.CampStatus;
import com.jeevadaana.model.Donor;
import com.jeevadaana.model.RegistrationStatus;
import com.jeevadaana.repository.CampRegistrationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final CampRegistrationRepository registrationRepository;

    public RegistrationService(CampRegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    @Transactional
    public CampRegistration register(Camp camp, Donor donor) {
        if (camp.getStatus() != CampStatus.UPCOMING) {
            throw new ServiceException("Registration is closed for this camp.");
        }
        if (registrationRepository.existsByCampAndDonor(camp, donor)) {
            throw new ServiceException("You are already registered for this camp.");
        }
        if (camp.getCapacity() != null) {
            long count = registrationRepository.countByCamp(camp);
            if (count >= camp.getCapacity()) {
                throw new ServiceException("This camp is already full.");
            }
        }
        CampRegistration registration = new CampRegistration();
        registration.setCamp(camp);
        registration.setDonor(donor);
        registration.setStatus(RegistrationStatus.REGISTERED);
        return registrationRepository.save(registration);
    }

    @Transactional
    public void cancel(Long registrationId, Donor donor) {
        CampRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ServiceException("Registration not found."));
        if (!registration.getDonor().getId().equals(donor.getId())) {
            throw new ServiceException("You cannot cancel this registration.");
        }
        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
    }

    public List<CampRegistration> listForDonor(Donor donor) {
        return registrationRepository.findByDonorOrderByRegisteredAtDesc(donor);
    }

    public List<CampRegistration> listForCamp(Camp camp) {
        return registrationRepository.findByCampOrderByRegisteredAtAsc(camp);
    }

    public boolean isRegistered(Camp camp, Donor donor) {
        return registrationRepository.findByCampAndDonor(camp, donor)
                .filter(r -> r.getStatus() != RegistrationStatus.CANCELLED)
                .isPresent();
    }

    public long countForCamp(Camp camp) {
        return registrationRepository.countByCamp(camp);
    }

    public CampRegistration getById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Registration not found."));
    }
}
