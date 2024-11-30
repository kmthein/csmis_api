package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.PaymentVoucherDTO;
import com.team2.csmis_api.entity.PaymentVoucher;

import java.time.LocalDate;
import java.util.List;

public interface PaymentVoucherService {
    String createPaymentVoucherByDate(LocalDate selectedDate, PaymentVoucherDTO requestDTO);

    void updatePaymentVoucher(Integer id, PaymentVoucherDTO requestDTO);  // Change Long to Integer

    void deletePaymentVoucher(Integer id);

    PaymentVoucher getPaymentVoucherById(Integer id);

    List<PaymentVoucher> getAllNonDeletedPaymentVouchers();


}