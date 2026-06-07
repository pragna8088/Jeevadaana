package com.jeevadaana.dto;

import com.jeevadaana.model.Camp;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * JSON view of a {@link Camp} returned by the REST API.
 */
public class CampResponse {

    private Long id;
    private String name;
    private String district;
    private String venue;
    private LocalDate campDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer capacity;
    private String description;
    private String status;
    private String organizerName;
    private String organizerContact;
    private String organizerPhone;
    private String organizerEmail;
    private Long registrationCount;

    public static CampResponse from(Camp camp) {
        CampResponse r = new CampResponse();
        r.id = camp.getId();
        r.name = camp.getName();
        r.district = camp.getDistrict();
        r.venue = camp.getVenue();
        r.campDate = camp.getCampDate();
        r.startTime = camp.getStartTime();
        r.endTime = camp.getEndTime();
        r.capacity = camp.getCapacity();
        r.description = camp.getDescription();
        r.status = camp.getStatus() != null ? camp.getStatus().name() : null;
        if (camp.getOrganizer() != null) {
            r.organizerName = camp.getOrganizer().getOrganizationName();
            r.organizerContact = camp.getOrganizer().getContactPerson();
            r.organizerPhone = camp.getOrganizer().getPhone();
            r.organizerEmail = camp.getOrganizer().getEmail();
        }
        return r;
    }

    public static CampResponse from(Camp camp, long registrationCount) {
        CampResponse r = from(camp);
        r.registrationCount = registrationCount;
        return r;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDistrict() {
        return district;
    }

    public String getVenue() {
        return venue;
    }

    public LocalDate getCampDate() {
        return campDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public String getOrganizerContact() {
        return organizerContact;
    }

    public String getOrganizerPhone() {
        return organizerPhone;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public Long getRegistrationCount() {
        return registrationCount;
    }
}
