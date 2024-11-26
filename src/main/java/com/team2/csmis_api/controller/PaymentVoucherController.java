package com.team2.csmis_api.controller;

import com.team2.csmis_api.dto.PaymentVoucherDTO;
import com.team2.csmis_api.dto.PaymentVoucherDateRequestDTO;
import com.team2.csmis_api.entity.PaymentVoucher;
import com.team2.csmis_api.service.PaymentVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/payment-vouchers")
@RequiredArgsConstructor
public class PaymentVoucherController {
    private final PaymentVoucherService paymentVoucherService;

    @PostMapping("/from-date")
    public ResponseEntity<PaymentVoucher> createPaymentVoucherFromDate(
            @RequestBody PaymentVoucherDateRequestDTO requestDTO) {
        LocalDate selectedDate = requestDTO.getSelectedDate();
        return ResponseEntity.ok(paymentVoucherService.createPaymentVoucherFromDate(selectedDate, requestDTO));
    }


    @PostMapping("/from-order/{orderId}")
    public ResponseEntity<PaymentVoucher> createPaymentVoucherFromOrder(
            @PathVariable Long orderId,
            @RequestBody PaymentVoucherDTO requestDTO) {
        return ResponseEntity.ok(paymentVoucherService.createPaymentVoucherFromOrder(orderId, requestDTO));
    }
}

//    // GET endpoint to create voucher based on selected date (testing)
//    @GetMapping("/create")
//    public String createVoucher(
//            @RequestParam("selectedDate")
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate) {
//        // Placeholder logic
//        System.out.println("Selected date (GET): " + selectedDate);
//        return "Voucher creation simulated for date: " + selectedDate;
//    }
//
//    // POST endpoint to create voucher from DTO
//    @PostMapping("/create")
//    public String createVoucher(@RequestBody PaymentVoucherDTO dto) {
//        // Ensure DTO contains the required data
//        if (dto.getSelectedDate() == null) {
//            throw new IllegalArgumentException("Selected date is required in the request body.");
//        }
//
//        // Extract date and simulate voucher creation
//        LocalDate selectedDate = dto.getSelectedDate();
//        System.out.println("Selected date (POST): " + selectedDate);
//        System.out.println("Voucher Details: " + dto);
//        return "Voucher creation simulated for date: " + selectedDate;
//    }
