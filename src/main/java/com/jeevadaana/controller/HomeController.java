package com.jeevadaana.controller;

import com.jeevadaana.model.Camp;
import com.jeevadaana.service.CampService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final CampService campService;

    public HomeController(CampService campService) {
        this.campService = campService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("upcomingCamps", campService.listUpcoming());
        return "index";
    }

    /** Public district-wise camp search. */
    @GetMapping("/camps")
    public String camps(@RequestParam(name = "district", required = false) String district, Model model) {
        List<Camp> camps = campService.searchUpcoming(district);
        model.addAttribute("camps", camps);
        model.addAttribute("districts", campService.listDistricts());
        model.addAttribute("district", district);
        return "camps";
    }

    /** Public camp detail page (loads full info dynamically via REST /api/camps/{id}). */
    @GetMapping("/camps/{id}")
    public String campDetail(@PathVariable("id") Long id, Model model) {
        Camp camp = campService.getById(id);
        model.addAttribute("camp", camp);
        model.addAttribute("registrationCount", campService.registrationCount(camp));
        return "camp-detail";
    }

    /** Blood donation instructions + donor eligibility guidelines. */
    @GetMapping("/guidelines")
    public String guidelines() {
        return "guidelines";
    }

    /** Instructions for organizers on how to run a camp. */
    @GetMapping("/organizer-info")
    public String organizerInfo() {
        return "organizer-info";
    }
}
