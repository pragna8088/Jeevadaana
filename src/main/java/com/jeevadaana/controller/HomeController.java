package com.jeevadaana.controller;

import com.jeevadaana.model.Camp;
import com.jeevadaana.service.CampService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
