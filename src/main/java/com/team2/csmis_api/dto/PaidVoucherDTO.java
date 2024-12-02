package com.team2.csmis_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
public class PaidVoucherDTO {
    private String voucherNo;
    private String invoiceFor;
    private String cashier;
    private String approvedBy;
    private String receivedBy;
    private Date payDate;
    private Double amount;
    private String status;
    private String paymentMethod;
}
