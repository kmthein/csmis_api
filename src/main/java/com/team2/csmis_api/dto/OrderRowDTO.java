package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderRowDTO {
    private Integer id;
    private LocalDate lunchDate;
    private Integer quantity;
}
