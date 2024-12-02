package com.team2.csmis_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LunchDetailsDTO {
    private int registeredDates;
    private double lunchPrice;
    private double companyRate;
    private double userCostPerDay;
    private double companyCostPerDay;
    private double userMonthlyCost;
    private double companyMonthlyCost;
    private double estMonthlyCost;
}
