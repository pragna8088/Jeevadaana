package com.jeevadaana.controller.api;

import com.jeevadaana.dto.CampResponse;
import com.jeevadaana.dto.CampStats;
import com.jeevadaana.model.Camp;
import com.jeevadaana.service.CampService;
import com.jeevadaana.service.DonationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public REST API consumed by the frontend (JavaScript fetch) for live
 * district-wise camp search, camp details and post-camp statistics.
 */
@RestController
@RequestMapping("/api")
public class CampApiController {

    private final CampService campService;
    private final DonationService donationService;

    public CampApiController(CampService campService, DonationService donationService) {
        this.campService = campService;
        this.donationService = donationService;
    }

    /** GET /api/camps?district=Bengaluru — upcoming camps, optionally filtered by district. */
    @GetMapping("/camps")
    public List<CampResponse> camps(@RequestParam(name = "district", required = false) String district) {
        return campService.searchUpcoming(district).stream()
                .map(camp -> CampResponse.from(camp, campService.registrationCount(camp)))
                .toList();
    }

    /** GET /api/camps/{id} — full detail for a single camp, including organizer contact. */
    @GetMapping("/camps/{id}")
    public CampResponse camp(@PathVariable("id") Long id) {
        Camp camp = campService.getById(id);
        return CampResponse.from(camp, campService.registrationCount(camp));
    }

    /** GET /api/camps/{id}/stats — post-camp statistics (totals + blood-group-wise). */
    @GetMapping("/camps/{id}/stats")
    public CampStats stats(@PathVariable("id") Long id) {
        Camp camp = campService.getById(id);
        return donationService.statsForCamp(camp);
    }

    /** GET /api/districts — distinct districts that currently have camps. */
    @GetMapping("/districts")
    public List<String> districts() {
        return campService.listDistricts();
    }
}
