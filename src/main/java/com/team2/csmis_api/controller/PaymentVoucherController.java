package com.team2.csmis_api.controller;


import com.team2.csmis_api.dto.PaymentVoucherDTO;
import com.team2.csmis_api.entity.PaymentVoucher;
import com.team2.csmis_api.service.PaymentVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;


@RestController
@RequestMapping("/api/payment-vouchers")
@RequiredArgsConstructor
public class PaymentVoucherController {

    private final PaymentVoucherService paymentVoucherService;

    @PostMapping("/from-date")
    public ResponseEntity<Void> createPaymentVoucherByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate,
            @RequestBody PaymentVoucherDTO requestDTO) {System.out.println(requestDTO.getStatus());
        paymentVoucherService.createPaymentVoucherByDate(selectedDate, requestDTO);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePaymentVoucher(
            @PathVariable Integer id,
            @RequestBody PaymentVoucherDTO requestDTO) {
        paymentVoucherService.updatePaymentVoucher(id, requestDTO);
        return ResponseEntity.ok().build();
    }

    // Delete Payment Voucher by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePaymentVoucher(@PathVariable Integer id) {
        paymentVoucherService.deletePaymentVoucher(id);
        return ResponseEntity.ok("Payment Voucher with ID " + id + " has been successfully deleted.");
    }

    // Get Payment Voucher by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentVoucher> getPaymentVoucherById(@PathVariable Integer id) {
        PaymentVoucher voucher = paymentVoucherService.getPaymentVoucherById(id);
        return ResponseEntity.ok(voucher);
    }
}
