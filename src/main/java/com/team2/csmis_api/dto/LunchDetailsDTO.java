package com.team2.csmis_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LunchDetailsDTO {
    private int registeredDays;
    private double lunchPrice;
    private double companyRate;
}
