package com.team2.csmis_api.dto;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PaymentVoucherDTO {
    private String voucherNo;
    private LocalDate paymentDate;
    private String restaurantName;
    private String invoiceFor;
    private List<VoucherRowDTO> rows;
    private String cashier;
    private String receivedBy;
    private String approvedBy;
    private String status; // "Paid" or "Unpaid"
}
//    @Override
//    public String toString() {
//        return "PaymentVoucherDTO{" +
//                "receivedBy='" + receivedBy + '\'' +
//                ", status='" + status + '\'' +
//                '}';
//    }
