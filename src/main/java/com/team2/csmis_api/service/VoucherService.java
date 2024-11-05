package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.VoucherDTO;
import com.team2.csmis_api.entity.Voucher;
import com.team2.csmis_api.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;

    public List<VoucherDTO> getVouchers(Date startDate, Date endDate) {
        return voucherRepository.findByPaymentDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private VoucherDTO convertToDto(Voucher voucher) {
        VoucherDTO dto = new VoucherDTO();
        dto.setVoucherCode(voucher.getVoucherCode());
        dto.setPeriod(voucher.getPeriod());
        dto.setCashier(voucher.getCashier());
        dto.setReceivedBy(voucher.getReceivedBy());
        dto.setApprover(voucher.getApprover());
        dto.setNumberOfPax(voucher.getNumberOfPax());
        dto.setPrice(voucher.getPrice());
        dto.setAmount(voucher.getAmount());
        dto.setPaymentDate(voucher.getPaymentDate());
        dto.setStatus(voucher.getStatus());
        dto.setPaymentMethod(voucher.getPaymentMethod());
        return dto;
    }
}
