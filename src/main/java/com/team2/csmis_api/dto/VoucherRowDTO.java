package com.team2.csmis_api.dto;

import com.team2.csmis_api.entity.OrderRow;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VoucherRowDTO {
    private Integer id;  // ID of the VoucherRow
    private LocalDate dt;  // Date of the VoucherRow
    private Integer qty;  // Quantity
    private Double price;  // Price
    private Double amount;  // Amount
    private String remark;  // Remark for the VoucherRow
}

