package com.team2.csmis_api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeCostDTO {
    private String name;
    private String staffId;
    private Double amount;
    private Double totalCost;
}
