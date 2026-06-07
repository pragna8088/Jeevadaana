package com.jeevadaana.controller.api;

import com.jeevadaana.dto.CampResponse;
import com.jeevadaana.service.CampService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public REST API consumed by the frontend (JavaScript fetch) for live
 * district-wise camp search.
 */
@RestController
@RequestMapping("/api")
public class CampApiController {

    private final CampService campService;

    public CampApiController(CampService campService) {
        this.campService = campService;
    }

    /** GET /api/camps?district=Bengaluru — upcoming camps, optionally filtered by district. */
    @GetMapping("/camps")
    public List<CampResponse> camps(@RequestParam(name = "district", required = false) String district) {
        return campService.searchUpcoming(district).stream()
                .map(CampResponse::from)
                .toList();
    }

    /** GET /api/districts — distinct districts that currently have camps. */
    @GetMapping("/districts")
    public List<String> districts() {
        return campService.listDistricts();
    }
}
