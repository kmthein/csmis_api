package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.PaymentVoucherDTO;
import com.team2.csmis_api.service.PaymentVoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-vouchers")
public class PaymentVoucherController {

    private final PaymentVoucherService paymentVoucherService;

    public PaymentVoucherController(PaymentVoucherService paymentVoucherService) {
        this.paymentVoucherService = paymentVoucherService;
    }

    @PostMapping
    public ResponseEntity<PaymentVoucherDTO> createPaymentVoucher(@RequestBody PaymentVoucherDTO paymentVoucherDTO) {
        PaymentVoucherDTO createdVoucher = paymentVoucherService.createPaymentVoucher(paymentVoucherDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentVoucherDTO> getPaymentVoucherById(@PathVariable Integer id) {
        PaymentVoucherDTO voucher = paymentVoucherService.getPaymentVoucherById(id);
        return ResponseEntity.ok(voucher);
    }

    @GetMapping
    public ResponseEntity<List<PaymentVoucherDTO>> getAllPaymentVouchers() {
        List<PaymentVoucherDTO> vouchers = paymentVoucherService.getAllPaymentVouchers();
        return ResponseEntity.ok(vouchers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentVoucherDTO> updatePaymentVoucher(
            @PathVariable Integer id,
            @RequestBody PaymentVoucherDTO updatedVoucherDTO
    ) {
        PaymentVoucherDTO updatedVoucher = paymentVoucherService.updatePaymentVoucher(id, updatedVoucherDTO);
        return ResponseEntity.ok(updatedVoucher);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentVoucher(@PathVariable Integer id) {
        paymentVoucherService.deletePaymentVoucher(id);
        return ResponseEntity.noContent().build();
    }
}
