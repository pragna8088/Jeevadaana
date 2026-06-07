package com.jeevadaana.config;

import com.jeevadaana.model.BloodGroup;
import com.jeevadaana.model.Camp;
import com.jeevadaana.model.CampStatus;
import com.jeevadaana.model.Donor;
import com.jeevadaana.model.Gender;
import com.jeevadaana.model.Organizer;
import com.jeevadaana.repository.CampRepository;
import com.jeevadaana.repository.DonorRepository;
import com.jeevadaana.repository.OrganizerRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a demo organizer, donor and a few camps on first startup so the app is
 * immediately explorable. Idempotent: only runs when the tables are empty.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final OrganizerRepository organizerRepository;
    private final DonorRepository donorRepository;
    private final CampRepository campRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(OrganizerRepository organizerRepository, DonorRepository donorRepository,
                      CampRepository campRepository, PasswordEncoder passwordEncoder) {
        this.organizerRepository = organizerRepository;
        this.donorRepository = donorRepository;
        this.campRepository = campRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (organizerRepository.count() > 0 || donorRepository.count() > 0) {
            return;
        }

        Organizer organizer = new Organizer();
        organizer.setOrganizationName("Red Cross Bengaluru");
        organizer.setContactPerson("Asha Rao");
        organizer.setEmail("organizer@jeevadaana.org");
        organizer.setPassword(passwordEncoder.encode("password"));
        organizer.setPhone("9000000001");
        organizer.setDistrict("Bengaluru");
        organizer.setAddress("MG Road, Bengaluru");
        organizer = organizerRepository.save(organizer);

        Donor donor = new Donor();
        donor.setName("Ravi Kumar");
        donor.setEmail("donor@jeevadaana.org");
        donor.setPassword(passwordEncoder.encode("password"));
        donor.setPhone("9000000002");
        donor.setBloodGroup(BloodGroup.O_POSITIVE);
        donor.setGender(Gender.MALE);
        donor.setAge(28);
        donor.setDistrict("Bengaluru");
        donor.setAddress("Indiranagar, Bengaluru");
        donorRepository.save(donor);

        campRepository.save(camp("City Blood Drive", organizer, "Bengaluru",
                "Town Hall, Bengaluru", LocalDate.now().plusDays(7), 100,
                "Annual city-wide blood donation drive."));
        campRepository.save(camp("Campus Donation Camp", organizer, "Mysuru",
                "University Auditorium, Mysuru", LocalDate.now().plusDays(14), 60,
                "Blood donation camp for students and staff."));
        campRepository.save(camp("Community Health Camp", organizer, "Bengaluru",
                "Community Center, Whitefield", LocalDate.now().plusDays(21), 80,
                "Health checkup and blood donation."));
    }

    private Camp camp(String name, Organizer organizer, String district, String venue,
                      LocalDate date, int capacity, String description) {
        Camp camp = new Camp();
        camp.setName(name);
        camp.setOrganizer(organizer);
        camp.setDistrict(district);
        camp.setVenue(venue);
        camp.setCampDate(date);
        camp.setStartTime(LocalTime.of(9, 0));
        camp.setEndTime(LocalTime.of(16, 0));
        camp.setCapacity(capacity);
        camp.setDescription(description);
        camp.setStatus(CampStatus.UPCOMING);
        return camp;
    }
}
