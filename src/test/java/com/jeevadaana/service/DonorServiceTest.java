package com.jeevadaana.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jeevadaana.dto.DonorRegistrationForm;
import com.jeevadaana.model.BloodGroup;
import com.jeevadaana.model.Donor;
import com.jeevadaana.model.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DonorServiceTest {

    @Autowired
    private DonorService donorService;

    private DonorRegistrationForm sampleForm(String email) {
        DonorRegistrationForm form = new DonorRegistrationForm();
        form.setName("Test Donor");
        form.setEmail(email);
        form.setPassword("secret123");
        form.setPhone("9876543210");
        form.setBloodGroup(BloodGroup.A_POSITIVE);
        form.setGender(Gender.MALE);
        form.setAge(25);
        form.setDistrict("Bengaluru");
        form.setAddress("Somewhere");
        return form;
    }

    @Test
    void registersAndHashesPassword() {
        Donor donor = donorService.register(sampleForm("a@b.com"));
        assertThat(donor.getId()).isNotNull();
        assertThat(donor.getPassword()).isNotEqualTo("secret123");
        assertThat(donor.getEmail()).isEqualTo("a@b.com");
    }

    @Test
    void authenticatesWithCorrectCredentials() {
        donorService.register(sampleForm("login@b.com"));
        Donor authed = donorService.authenticate("login@b.com", "secret123");
        assertThat(authed.getEmail()).isEqualTo("login@b.com");
    }

    @Test
    void rejectsDuplicateEmailAndBadCredentials() {
        donorService.register(sampleForm("dup@b.com"));
        assertThatThrownBy(() -> donorService.register(sampleForm("dup@b.com")))
                .isInstanceOf(ServiceException.class);
        assertThatThrownBy(() -> donorService.authenticate("dup@b.com", "wrong"))
                .isInstanceOf(ServiceException.class);
    }
}
