package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.PaymentVoucherDTO;
import com.team2.csmis_api.dto.VoucherRowDTO;
import com.team2.csmis_api.entity.PaymentVoucher;
import com.team2.csmis_api.entity.VoucherRow;
import com.team2.csmis_api.repository.PaymentVoucherRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentVoucherService {

    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Transactional
    public PaymentVoucherDTO createPaymentVoucher(PaymentVoucherDTO paymentVoucherDTO) {
        PaymentVoucher paymentVoucher = modelMapper.map(paymentVoucherDTO, PaymentVoucher.class);

        // Set reference for VoucherRows
        paymentVoucher.getRows().forEach(row -> row.setPaymentVoucher(paymentVoucher));

        PaymentVoucher savedVoucher = paymentVoucherRepository.save(paymentVoucher);
        return modelMapper.map(savedVoucher, PaymentVoucherDTO.class);
    }

    public PaymentVoucherDTO getPaymentVoucherById(Integer id) {
        PaymentVoucher paymentVoucher = paymentVoucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentVoucher not found for ID: " + id));
        return modelMapper.map(paymentVoucher, PaymentVoucherDTO.class);
    }

    public List<PaymentVoucherDTO> getAllPaymentVouchers() {
        return paymentVoucherRepository.findAll()
                .stream()
                .map(voucher -> modelMapper.map(voucher, PaymentVoucherDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentVoucherDTO updatePaymentVoucher(Integer id, PaymentVoucherDTO updatedVoucherDTO) {
        PaymentVoucher existingVoucher = paymentVoucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentVoucher not found for ID: " + id));

        // Update fields
        existingVoucher.setVoucherNo(updatedVoucherDTO.getVoucherNo());
        existingVoucher.setRestaurantName(updatedVoucherDTO.getRestaurantName());
        existingVoucher.setPaymentDate(updatedVoucherDTO.getPaymentDate());
        existingVoucher.setInvoiceFor(updatedVoucherDTO.getInvoiceFor());
        existingVoucher.setCashier(updatedVoucherDTO.getCashier());
        existingVoucher.setReceivedBy(updatedVoucherDTO.getReceivedBy());
        existingVoucher.setApprovedBy(updatedVoucherDTO.getApprovedBy());

        // Clear and add new rows
        existingVoucher.getRows().clear();
        updatedVoucherDTO.getRows().forEach(rowDTO -> {
            VoucherRow row = modelMapper.map(rowDTO, VoucherRow.class);
            row.setPaymentVoucher(existingVoucher);
            existingVoucher.getRows().add(row);
        });

        PaymentVoucher updatedVoucher = paymentVoucherRepository.save(existingVoucher);
        return modelMapper.map(updatedVoucher, PaymentVoucherDTO.class);
    }

    public void deletePaymentVoucher(Integer id) {
        paymentVoucherRepository.deleteById(id);
    }
}
