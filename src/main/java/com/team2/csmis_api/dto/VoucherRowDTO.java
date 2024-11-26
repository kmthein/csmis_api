package com.team2.csmis_api.dto;

import com.team2.csmis_api.entity.OrderRow;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VoucherRowDTO {

    private LocalDate dt;
    private Integer qty;
    private Double price;
    private Double amount;
    private String remark;
}
