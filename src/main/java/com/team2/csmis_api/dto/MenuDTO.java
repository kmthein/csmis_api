package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MenuDTO {
    private LocalDate date;
    private String menu;
}
