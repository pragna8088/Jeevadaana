package com.jeevadaana.dto;

import com.jeevadaana.model.BloodGroup;
import com.jeevadaana.model.Donation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregated post-camp statistics: total registrations, donors, units collected
 * and a blood-group-wise breakdown. Computed from persisted MySQL data.
 */
public class CampStats {

    private long totalRegistrations;
    private long totalDonors;
    private int totalUnits;
    private List<BloodGroupStat> byBloodGroup = new ArrayList<>();

    public static CampStats from(long totalRegistrations, List<Donation> donations) {
        CampStats stats = new CampStats();
        stats.totalRegistrations = totalRegistrations;
        stats.totalDonors = donations.size();

        Map<BloodGroup, int[]> grouped = new LinkedHashMap<>();
        int totalUnits = 0;
        for (Donation d : donations) {
            int units = d.getUnitsMl() != null ? d.getUnitsMl() : 0;
            totalUnits += units;
            int[] agg = grouped.computeIfAbsent(d.getBloodGroup(), k -> new int[2]);
            agg[0] += 1;      // donors
            agg[1] += units;  // units
        }
        stats.totalUnits = totalUnits;
        for (Map.Entry<BloodGroup, int[]> e : grouped.entrySet()) {
            stats.byBloodGroup.add(new BloodGroupStat(
                    e.getKey().getLabel(), e.getValue()[0], e.getValue()[1]));
        }
        return stats;
    }

    public long getTotalRegistrations() {
        return totalRegistrations;
    }

    public long getTotalDonors() {
        return totalDonors;
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public List<BloodGroupStat> getByBloodGroup() {
        return byBloodGroup;
    }

    /** Blood-group-wise collection row. */
    public static class BloodGroupStat {
        private final String bloodGroup;
        private final int donors;
        private final int units;

        public BloodGroupStat(String bloodGroup, int donors, int units) {
            this.bloodGroup = bloodGroup;
            this.donors = donors;
            this.units = units;
        }

        public String getBloodGroup() {
            return bloodGroup;
        }

        public int getDonors() {
            return donors;
        }

        public int getUnits() {
            return units;
        }
    }
}
