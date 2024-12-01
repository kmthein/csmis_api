package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.PaymentVoucherDTO;
import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.entity.PaymentVoucher;
import com.team2.csmis_api.entity.VoucherRow;

import java.time.LocalDate;
import java.util.List;

public interface PaymentVoucherService {
    List<PaymentVoucher> getAllPaymentVoucher();

    List<VoucherRow> getAlreadyHaveVoucherDates();

    String createPaymentVoucherByDate(LocalDate selectedDate, PaymentVoucherDTO requestDTO);

    ResponseDTO updatePaymentVoucher(Integer id, PaymentVoucherDTO requestDTO);  // Change Long to Integer

    void deletePaymentVoucher(Integer id);

    PaymentVoucher getPaymentVoucherById(Integer id);
}