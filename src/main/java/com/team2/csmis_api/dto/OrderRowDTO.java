package com.team2.csmis_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class OrderRowDTO {
    private Integer id;
    private LocalDate lunchDate;
    private Integer quantity;

    public OrderRowDTO(Long id, LocalDate lunchDate) {
        this.id = Math.toIntExact(id);
        this.lunchDate = lunchDate;
    }

}