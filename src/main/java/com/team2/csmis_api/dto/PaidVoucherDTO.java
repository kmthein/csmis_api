package com.team2.csmis_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PaidVoucherDTO {
    private String voucherNo;
    private String invoiceFor;
    private String cashier;
    private String approvedBy;
    private String receivedBy;
    private LocalDate payDate;
    private Double mount;
    private String status;

}
