package com.jeevadaana.service;

import com.jeevadaana.dto.CampForm;
import com.jeevadaana.model.Camp;
import com.jeevadaana.model.CampStatus;
import com.jeevadaana.model.Organizer;
import com.jeevadaana.repository.CampRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampService {

    private final CampRepository campRepository;

    public CampService(CampRepository campRepository) {
        this.campRepository = campRepository;
    }

    @Transactional
    public Camp create(CampForm form, Organizer organizer) {
        Camp camp = new Camp();
        applyForm(camp, form);
        camp.setOrganizer(organizer);
        camp.setStatus(CampStatus.UPCOMING);
        return campRepository.save(camp);
    }

    @Transactional
    public Camp update(Long campId, CampForm form, Organizer organizer) {
        Camp camp = getOwnedCamp(campId, organizer);
        applyForm(camp, form);
        return campRepository.save(camp);
    }

    private void applyForm(Camp camp, CampForm form) {
        camp.setName(form.getName().trim());
        camp.setDistrict(form.getDistrict().trim());
        camp.setVenue(form.getVenue().trim());
        camp.setCampDate(form.getCampDate());
        camp.setStartTime(form.getStartTime());
        camp.setEndTime(form.getEndTime());
        camp.setCapacity(form.getCapacity());
        camp.setDescription(form.getDescription());
    }

    @Transactional
    public void updateStatus(Long campId, CampStatus status, Organizer organizer) {
        Camp camp = getOwnedCamp(campId, organizer);
        camp.setStatus(status);
        campRepository.save(camp);
    }

    public Camp getById(Long campId) {
        return campRepository.findById(campId)
                .orElseThrow(() -> new ServiceException("Camp not found."));
    }

    public Camp getOwnedCamp(Long campId, Organizer organizer) {
        Camp camp = getById(campId);
        if (!camp.getOrganizer().getId().equals(organizer.getId())) {
            throw new ServiceException("You are not allowed to manage this camp.");
        }
        return camp;
    }

    public List<Camp> listByOrganizer(Organizer organizer) {
        return campRepository.findByOrganizerOrderByCampDateDesc(organizer);
    }

    public List<Camp> listUpcoming() {
        return campRepository.findByCampDateGreaterThanEqualAndStatusOrderByCampDateAsc(
                LocalDate.now(), CampStatus.UPCOMING);
    }

    /** District-wise search across upcoming camps. Blank district returns all upcoming camps. */
    public List<Camp> searchUpcoming(String district) {
        return campRepository.searchUpcoming(district == null ? "" : district.trim(), CampStatus.UPCOMING);
    }

    public List<Camp> nearbyCamps(String district) {
        return campRepository.findByDistrictIgnoreCaseAndStatusOrderByCampDateAsc(district, CampStatus.UPCOMING);
    }

    public List<String> listDistricts() {
        return campRepository.findDistinctDistricts();
    }
}
