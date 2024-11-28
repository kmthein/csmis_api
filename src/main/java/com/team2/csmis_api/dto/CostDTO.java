package com.team2.csmis_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;


@Data
@AllArgsConstructor
public class CostDTO {

    private Date date;
    private Integer quantity;
    private Long userId;
    private Double price;
    private Double companyRate;
    private Double companyCost;
    private Double totalCost;

}
