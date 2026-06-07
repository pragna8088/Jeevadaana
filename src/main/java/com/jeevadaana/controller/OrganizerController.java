package com.jeevadaana.controller;

import com.jeevadaana.config.SessionKeys;
import com.jeevadaana.dto.CampForm;
import com.jeevadaana.dto.LoginForm;
import com.jeevadaana.dto.OrganizerRegistrationForm;
import com.jeevadaana.dto.RecordDonationForm;
import com.jeevadaana.model.Camp;
import com.jeevadaana.model.CampStatus;
import com.jeevadaana.model.CampRegistration;
import com.jeevadaana.model.Organizer;
import com.jeevadaana.service.CampService;
import com.jeevadaana.service.DonationService;
import com.jeevadaana.service.OrganizerService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/organizer")
public class OrganizerController {

    private final OrganizerService organizerService;
    private final CampService campService;
    private final RegistrationService registrationService;
    private final DonationService donationService;

    public OrganizerController(OrganizerService organizerService, CampService campService,
                              RegistrationService registrationService, DonationService donationService) {
        this.organizerService = organizerService;
        this.campService = campService;
        this.registrationService = registrationService;
        this.donationService = donationService;
    }

    // ---------- Registration ----------

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new OrganizerRegistrationForm());
        return "organizer/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") OrganizerRegistrationForm form,
                           BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "organizer/register";
        }
        try {
            organizerService.register(form);
        } catch (ServiceException ex) {
            model.addAttribute("error", ex.getMessage());
            return "organizer/register";
        }
        ra.addFlashAttribute("success", "Registration successful. Please log in.");
        return "redirect:/organizer/login";
    }

    // ---------- Login / Logout ----------

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("form", new LoginForm());
        return "organizer/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("form") LoginForm form, BindingResult result,
                        Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "organizer/login";
        }
        try {
            Organizer organizer = organizerService.authenticate(form.getEmail(), form.getPassword());
            session.setAttribute(SessionKeys.ORGANIZER_ID, organizer.getId());
            session.setAttribute(SessionKeys.ORGANIZER_NAME, organizer.getOrganizationName());
        } catch (ServiceException ex) {
            model.addAttribute("error", ex.getMessage());
            return "organizer/login";
        }
        return "redirect:/organizer/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(SessionKeys.ORGANIZER_ID);
        session.removeAttribute(SessionKeys.ORGANIZER_NAME);
        return "redirect:/";
    }

    // ---------- Dashboard ----------

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Organizer organizer = currentOrganizer(session);
        var camps = campService.listByOrganizer(organizer);
        model.addAttribute("organizer", organizer);
        model.addAttribute("camps", camps);
        long upcoming = camps.stream().filter(c -> c.getStatus() == CampStatus.UPCOMING).count();
        long completed = camps.stream().filter(c -> c.getStatus() == CampStatus.COMPLETED).count();
        model.addAttribute("upcomingCount", upcoming);
        model.addAttribute("completedCount", completed);
        model.addAttribute("totalCamps", camps.size());
        return "organizer/dashboard";
    }

    // ---------- Camp organization ----------

    @GetMapping("/camps/new")
    public String newCampForm(HttpSession session, Model model) {
        currentOrganizer(session);
        model.addAttribute("form", new CampForm());
        model.addAttribute("mode", "new");
        return "organizer/camp-form";
    }

    @PostMapping("/camps/new")
    public String createCamp(@Valid @ModelAttribute("form") CampForm form, BindingResult result,
                             HttpSession session, Model model, RedirectAttributes ra) {
        Organizer organizer = currentOrganizer(session);
        if (result.hasErrors()) {
            model.addAttribute("mode", "new");
            return "organizer/camp-form";
        }
        Camp camp = campService.create(form, organizer);
        ra.addFlashAttribute("success", "Camp '" + camp.getName() + "' created.");
        return "redirect:/organizer/dashboard";
    }

    @GetMapping("/camps/{id}/edit")
    public String editCampForm(@PathVariable("id") Long campId, HttpSession session, Model model) {
        Organizer organizer = currentOrganizer(session);
        Camp camp = campService.getOwnedCamp(campId, organizer);
        CampForm form = new CampForm();
        form.setName(camp.getName());
        form.setDistrict(camp.getDistrict());
        form.setVenue(camp.getVenue());
        form.setCampDate(camp.getCampDate());
        form.setStartTime(camp.getStartTime());
        form.setEndTime(camp.getEndTime());
        form.setCapacity(camp.getCapacity());
        form.setDescription(camp.getDescription());
        model.addAttribute("form", form);
        model.addAttribute("campId", campId);
        model.addAttribute("mode", "edit");
        return "organizer/camp-form";
    }

    @PostMapping("/camps/{id}/edit")
    public String updateCamp(@PathVariable("id") Long campId, @Valid @ModelAttribute("form") CampForm form,
                             BindingResult result, HttpSession session, Model model, RedirectAttributes ra) {
        Organizer organizer = currentOrganizer(session);
        if (result.hasErrors()) {
            model.addAttribute("mode", "edit");
            model.addAttribute("campId", campId);
            return "organizer/camp-form";
        }
        campService.update(campId, form, organizer);
        ra.addFlashAttribute("success", "Camp updated.");
        return "redirect:/organizer/dashboard";
    }

    @PostMapping("/camps/{id}/cancel")
    public String cancelCamp(@PathVariable("id") Long campId, HttpSession session, RedirectAttributes ra) {
        Organizer organizer = currentOrganizer(session);
        try {
            campService.updateStatus(campId, CampStatus.CANCELLED, organizer);
            ra.addFlashAttribute("success", "Camp cancelled.");
        } catch (ServiceException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/organizer/dashboard";
    }

    @PostMapping("/camps/{id}/complete")
    public String completeCamp(@PathVariable("id") Long campId, HttpSession session, RedirectAttributes ra) {
        Organizer organizer = currentOrganizer(session);
        try {
            campService.updateStatus(campId, CampStatus.COMPLETED, organizer);
            ra.addFlashAttribute("success", "Camp marked as completed.");
        } catch (ServiceException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/organizer/camps/" + campId + "/manage";
    }

    // ---------- Post-camp management ----------

    @GetMapping("/camps/{id}/manage")
    public String manageCamp(@PathVariable("id") Long campId, HttpSession session, Model model) {
        Organizer organizer = currentOrganizer(session);
        Camp camp = campService.getOwnedCamp(campId, organizer);
        model.addAttribute("organizer", organizer);
        model.addAttribute("camp", camp);
        model.addAttribute("registrations", registrationService.listForCamp(camp));
        model.addAttribute("donations", donationService.listForCamp(camp));
        model.addAttribute("recordForm", new RecordDonationForm());
        return "organizer/camp-manage";
    }

    @PostMapping("/camps/{id}/record")
    public String recordDonation(@PathVariable("id") Long campId,
                                 @Valid @ModelAttribute("recordForm") RecordDonationForm form,
                                 BindingResult result, HttpSession session, RedirectAttributes ra) {
        Organizer organizer = currentOrganizer(session);
        try {
            Camp camp = campService.getOwnedCamp(campId, organizer);
            CampRegistration registration = registrationService.getById(form.getRegistrationId());
            if (!registration.getCamp().getId().equals(camp.getId())) {
                throw new ServiceException("Registration does not belong to this camp.");
            }
            donationService.recordFromRegistration(registration, form.getUnitsMl(), form.getRemarks());
            ra.addFlashAttribute("success",
                    "Donation recorded for " + registration.getDonor().getName() + ".");
        } catch (ServiceException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/organizer/camps/" + campId + "/manage";
    }

    private Organizer currentOrganizer(HttpSession session) {
        Long id = (Long) session.getAttribute(SessionKeys.ORGANIZER_ID);
        return organizerService.getById(id);
    }
}
