package com.team2.csmis_api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PaymentVoucherDTO {
    private Integer id;
    private String voucherNo;
    private String restaurantName;
    private LocalDate paymentDate;
    private String invoiceFor;
    private List<VoucherRowDTO> rows;
    private String cashier;
    private String receivedBy;
    private String approvedBy;
}
