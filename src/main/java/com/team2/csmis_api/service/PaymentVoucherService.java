package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.PaymentVoucherDTO;
import com.team2.csmis_api.dto.PaymentVoucherDateRequestDTO;
import com.team2.csmis_api.entity.PaymentVoucher;
import java.time.LocalDate;

public interface PaymentVoucherService {

//    PaymentVoucher createPaymentVoucherFromOrderByDate(LocalDate selectedDate, PaymentVoucherDTO requestDTO);
    PaymentVoucher createPaymentVoucherFromDate(LocalDate selectedDate, PaymentVoucherDateRequestDTO requestDTO);
    PaymentVoucher createPaymentVoucherFromOrder(Long orderId, PaymentVoucherDTO requestDTO);
}
