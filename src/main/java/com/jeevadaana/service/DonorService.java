package com.jeevadaana.service;

import com.jeevadaana.dto.DonorRegistrationForm;
import com.jeevadaana.model.Donor;
import com.jeevadaana.repository.DonorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonorService {

    private final DonorRepository donorRepository;
    private final PasswordEncoder passwordEncoder;

    public DonorService(DonorRepository donorRepository, PasswordEncoder passwordEncoder) {
        this.donorRepository = donorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Donor register(DonorRegistrationForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (donorRepository.existsByEmail(email)) {
            throw new ServiceException("An account with this email already exists.");
        }
        Donor donor = new Donor();
        donor.setName(form.getName().trim());
        donor.setEmail(email);
        donor.setPassword(passwordEncoder.encode(form.getPassword()));
        donor.setPhone(form.getPhone().trim());
        donor.setBloodGroup(form.getBloodGroup());
        donor.setGender(form.getGender());
        donor.setAge(form.getAge());
        donor.setDistrict(form.getDistrict().trim());
        donor.setAddress(form.getAddress());
        donor = donorRepository.save(donor);
        // Generate a human-friendly Registration ID once the numeric id is known.
        donor.setRegistrationCode(String.format("DNR-%06d", donor.getId()));
        return donorRepository.save(donor);
    }

    public Donor authenticate(String email, String rawPassword) {
        Donor donor = donorRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ServiceException("Invalid email or password."));
        if (!passwordEncoder.matches(rawPassword, donor.getPassword())) {
            throw new ServiceException("Invalid email or password.");
        }
        return donor;
    }

    public Donor getById(Long id) {
        return donorRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Donor not found."));
    }
}
