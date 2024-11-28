package com.team2.csmis_api.dto;


import lombok.Data;

import java.util.List;

@Data
public class PaymentVoucherDTO {
    private Integer cashierUserId;  // ID of the cashier selected from UI
    private Integer approvedByUserId;  // ID of the ADMIN selected from UI
    private String remark;  // Remark input by the cashier
    private String receivedBy;  // Who received the voucher
    private String status;  // Payment status (Paid/Unpaid)
    private List<VoucherRowDTO> rows;  // List of VoucherRowDTO for rows

}