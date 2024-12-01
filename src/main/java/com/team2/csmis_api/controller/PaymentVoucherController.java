package com.team2.csmis_api.controller;


import com.team2.csmis_api.dto.PaymentVoucherDTO;
import com.team2.csmis_api.dto.ResponseDTO;
import com.team2.csmis_api.entity.PaymentVoucher;
import com.team2.csmis_api.entity.VoucherRow;
import com.team2.csmis_api.service.PaymentVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/payment-vouchers")
@RequiredArgsConstructor
public class PaymentVoucherController {

    private final PaymentVoucherService paymentVoucherService;

    @GetMapping
    public List<PaymentVoucher> getAllPaymentVouchers() {
        return paymentVoucherService.getAllPaymentVoucher();
    }

    @GetMapping("/get-dates")
    public List<VoucherRow> getAlreadyHaveVoucherDates() {
        return paymentVoucherService.getAlreadyHaveVoucherDates();
    }

    @PostMapping("/from-date")
    public ResponseEntity<Void> createPaymentVoucherByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate,
            @RequestBody PaymentVoucherDTO requestDTO) {
        paymentVoucherService.createPaymentVoucherByDate(selectedDate, requestDTO);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}")
    public ResponseDTO updatePaymentVoucher(
            @PathVariable Integer id,
            @RequestBody PaymentVoucherDTO requestDTO) {
        ResponseDTO res = paymentVoucherService.updatePaymentVoucher(id, requestDTO);
        return res;
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
