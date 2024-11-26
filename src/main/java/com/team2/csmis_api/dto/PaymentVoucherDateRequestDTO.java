package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentVoucherDateRequestDTO {
    private LocalDate selectedDate;
    private String receivedBy;
    private String status; // Should be "PAID" or "UNPAID"
}