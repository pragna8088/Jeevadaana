package com.jeevadaana.service;

import com.jeevadaana.dto.OrganizerRegistrationForm;
import com.jeevadaana.model.Organizer;
import com.jeevadaana.repository.OrganizerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;

    public OrganizerService(OrganizerRepository organizerRepository, PasswordEncoder passwordEncoder) {
        this.organizerRepository = organizerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Organizer register(OrganizerRegistrationForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (organizerRepository.existsByEmail(email)) {
            throw new ServiceException("An account with this email already exists.");
        }
        Organizer organizer = new Organizer();
        organizer.setOrganizationName(form.getOrganizationName().trim());
        organizer.setContactPerson(form.getContactPerson().trim());
        organizer.setEmail(email);
        organizer.setPassword(passwordEncoder.encode(form.getPassword()));
        organizer.setPhone(form.getPhone().trim());
        organizer.setDistrict(form.getDistrict().trim());
        organizer.setAddress(form.getAddress());
        return organizerRepository.save(organizer);
    }

    public Organizer authenticate(String email, String rawPassword) {
        Organizer organizer = organizerRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ServiceException("Invalid email or password."));
        if (!passwordEncoder.matches(rawPassword, organizer.getPassword())) {
            throw new ServiceException("Invalid email or password.");
        }
        return organizer;
    }

    public Organizer getById(Long id) {
        return organizerRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Organizer not found."));
    }
}
