package com.team2.csmis_api.dto;

import lombok.Data;

@Data
public class VoucherDTO {
    private String voucherCode;
    private String period;
    private String cashier;
    private String receivedBy;
    private String approver;
    private int numberOfPax;
    private int price;
    private int amount;
    private String paymentDate;
    private String status;
    private String paymentMethod;
}

