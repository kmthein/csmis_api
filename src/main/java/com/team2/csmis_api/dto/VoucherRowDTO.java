package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VoucherRowDTO {
    private Long id;
    private LocalDate lunchDate;
    private Integer pax;
    private Double price;
    private Double amount;
    private String remark;
}
