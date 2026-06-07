package com.jeevadaana.controller;

import com.jeevadaana.config.SessionKeys;
import com.jeevadaana.dto.DonorRegistrationForm;
import com.jeevadaana.dto.LoginForm;
import com.jeevadaana.model.BloodGroup;
import com.jeevadaana.model.Camp;
import com.jeevadaana.model.Donor;
import com.jeevadaana.model.Gender;
import com.jeevadaana.service.CampService;
import com.jeevadaana.service.DonationService;
import com.jeevadaana.service.DonorService;
import com.jeevadaana.service.RegistrationService;
import com.jeevadaana.service.ServiceException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/donor")
public class DonorController {

    private final DonorService donorService;
    private final CampService campService;
    private final RegistrationService registrationService;
    private final DonationService donationService;

    public DonorController(DonorService donorService, CampService campService,
                           RegistrationService registrationService, DonationService donationService) {
        this.donorService = donorService;
        this.campService = campService;
        this.registrationService = registrationService;
        this.donationService = donationService;
    }

    // ---------- Registration ----------

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new DonorRegistrationForm());
        addReferenceData(model);
        return "donor/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") DonorRegistrationForm form,
                           BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            addReferenceData(model);
            return "donor/register";
        }
        Donor donor;
        try {
            donor = donorService.register(form);
        } catch (ServiceException ex) {
            model.addAttribute("error", ex.getMessage());
            addReferenceData(model);
            return "donor/register";
        }
        ra.addFlashAttribute("success",
                "Registration successful. Your Registration ID is " + donor.getRegistrationCode()
                        + ". Please log in.");
        return "redirect:/donor/login";
    }

    // ---------- Login / Logout ----------

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("form", new LoginForm());
        return "donor/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("form") LoginForm form, BindingResult result,
                        Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "donor/login";
        }
        try {
            Donor donor = donorService.authenticate(form.getEmail(), form.getPassword());
            session.setAttribute(SessionKeys.DONOR_ID, donor.getId());
            session.setAttribute(SessionKeys.DONOR_NAME, donor.getName());
        } catch (ServiceException ex) {
            model.addAttribute("error", ex.getMessage());
            return "donor/login";
        }
        return "redirect:/donor/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(SessionKeys.DONOR_ID);
        session.removeAttribute(SessionKeys.DONOR_NAME);
        return "redirect:/";
    }

    // ---------- Dashboard ----------

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Donor donor = currentDonor(session);
        var registrations = registrationService.listForDonor(donor);
        var activeCampIds = registrations.stream()
                .filter(r -> r.getStatus() != com.jeevadaana.model.RegistrationStatus.CANCELLED)
                .map(r -> r.getCamp().getId())
                .toList();
        model.addAttribute("donor", donor);
        model.addAttribute("donations", donationService.historyForDonor(donor));
        model.addAttribute("donationCount", donationService.countForDonor(donor));
        model.addAttribute("registrations", registrations);
        model.addAttribute("registeredCampIds", activeCampIds);
        model.addAttribute("nearbyCamps", campService.nearbyCamps(donor.getDistrict()));
        return "donor/dashboard";
    }

    // ---------- Camp registration ----------

    @PostMapping("/camps/{id}/register")
    public String registerForCamp(@PathVariable("id") Long campId, HttpSession session,
                                  RedirectAttributes ra) {
        Donor donor = currentDonor(session);
        try {
            Camp camp = campService.getById(campId);
            registrationService.register(camp, donor);
            ra.addFlashAttribute("success", "You are registered for " + camp.getName() + ".");
        } catch (ServiceException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/donor/dashboard";
    }

    @PostMapping("/registrations/{id}/cancel")
    public String cancelRegistration(@PathVariable("id") Long registrationId, HttpSession session,
                                     RedirectAttributes ra) {
        Donor donor = currentDonor(session);
        try {
            registrationService.cancel(registrationId, donor);
            ra.addFlashAttribute("success", "Registration cancelled.");
        } catch (ServiceException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/donor/dashboard";
    }

    // ---------- Donation history ----------

    @GetMapping("/donations")
    public String donations(HttpSession session, Model model) {
        Donor donor = currentDonor(session);
        model.addAttribute("donor", donor);
        model.addAttribute("donations", donationService.historyForDonor(donor));
        return "donor/donations";
    }

    // ---------- Camp search for donors ----------

    @GetMapping("/camps")
    public String searchCamps(@RequestParam(name = "district", required = false) String district,
                              HttpSession session, Model model) {
        Donor donor = currentDonor(session);
        model.addAttribute("donor", donor);
        model.addAttribute("camps", campService.searchUpcoming(district));
        model.addAttribute("districts", campService.listDistricts());
        model.addAttribute("district", district);
        model.addAttribute("registrations", registrationService.listForDonor(donor));
        return "donor/camps";
    }

    private Donor currentDonor(HttpSession session) {
        Long id = (Long) session.getAttribute(SessionKeys.DONOR_ID);
        return donorService.getById(id);
    }

    private void addReferenceData(Model model) {
        model.addAttribute("bloodGroups", BloodGroup.values());
        model.addAttribute("genders", Gender.values());
    }
}
