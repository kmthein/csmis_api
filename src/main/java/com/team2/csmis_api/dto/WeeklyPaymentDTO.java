package com.team2.csmis_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyPaymentDTO {
    private String week; // e.g., "Week 1"
    private String userName;
    private double totalCost;

}
