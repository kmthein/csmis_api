package com.team2.csmis_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class DateCountDTO {
    private LocalDate lunchDate;
    private Long quantity;
    private Double cost;
    public LocalDate convertToLocalDateViaSqlDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    public DateCountDTO(Date lunchDate, Long quantity, Double cost) {
        this.lunchDate = convertToLocalDateViaSqlDate(lunchDate);
        this.quantity = quantity;
        this.cost = cost;
    }
}
