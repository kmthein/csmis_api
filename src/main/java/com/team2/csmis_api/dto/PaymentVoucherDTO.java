package com.team2.csmis_api.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentVoucherDTO {
    private Integer cashierUserId;  // ID of the cashier selected from UI
    private Integer approvedByUserId;  // ID of the ADMIN selected from UI
    private String approvedByName;
    private String remark;  // Remark input by the cashier
    private String receivedBy;  // Who received the voucher
    private String status;  // Payment status (Paid/Unpaid)
    private Double totalAmount;
    private List<VoucherRowDTO> rows;  // List of VoucherRowDTO for rows
    private String paymentMethod = "Cash";
}