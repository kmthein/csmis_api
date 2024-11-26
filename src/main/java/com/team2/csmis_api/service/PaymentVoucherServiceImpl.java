package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.PaymentVoucherDTO;
import com.team2.csmis_api.dto.PaymentVoucherDateRequestDTO;
import com.team2.csmis_api.entity.*;
import com.team2.csmis_api.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentVoucherServiceImpl implements PaymentVoucherService {

    private final PaymentVoucherRepository paymentVoucherRepository;
    private final OrderRepository orderRepository;
    private final SettingRepository settingRepository;
    private final UserRepository userRepository;

    private final OrderRowRepository orderRowRepository;

    @Override
    @Transactional
    public PaymentVoucher createPaymentVoucherFromDate(LocalDate selectedDate, PaymentVoucherDateRequestDTO requestDTO) {
        if (requestDTO == null || selectedDate == null) {
            throw new IllegalArgumentException("Selected date and request details are required");
        }

        // Fetch the Order by filtering OrderRows with the selected date
        Order order = orderRepository.findOrderByLunchDate(selectedDate)
                .orElseThrow(() -> new RuntimeException("No order found for the selected date"));

        // Create a new PaymentVoucher entity
        PaymentVoucher paymentVoucher = new PaymentVoucher();
        paymentVoucher.setVoucherNo("V-" + System.currentTimeMillis());
        paymentVoucher.setPaymentDate(LocalDate.now());
        paymentVoucher.setRestaurantName(order.getRestaurant().getName());
        paymentVoucher.setInvoiceFor("Order on " + selectedDate);
        paymentVoucher.setCashier(requestDTO.getReceivedBy());
        paymentVoucher.setReceivedBy(requestDTO.getReceivedBy());
        paymentVoucher.setApprovedBy("Default Approver");
        paymentVoucher.setStatus(PaymentVoucher.PaymentStatus.valueOf(requestDTO.getStatus().toUpperCase()));

        // Save and return
        return paymentVoucherRepository.save(paymentVoucher);
    }



    @Override
    @Transactional
    public PaymentVoucher createPaymentVoucherFromOrder(Long orderId, PaymentVoucherDTO requestDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String year = String.valueOf(LocalDate.now().getYear());
        String month = String.format("%02d", LocalDate.now().getMonthValue());

        // Fetch the latest voucher number
        Optional<String> latestVoucherNo = paymentVoucherRepository.findLatestVoucherNo(year, month);
        String newVoucherNo = generateVoucherNo(latestVoucherNo, year, month);

        User cashier = order.getUser();

        // Fetch the first ADMIN user
        List<User> adminUsers = userRepository.findByRole(Role.ADMIN);
        if (adminUsers.isEmpty()) {
            throw new RuntimeException("No ADMIN user found");
        }
        User approvedBy = adminUsers.get(0); // Get the first ADMIN user

        PaymentVoucher voucher = new PaymentVoucher();
        voucher.setVoucherNo(newVoucherNo);
        voucher.setPaymentDate(LocalDate.now());
        voucher.setRestaurantName(order.getRestaurant().getName());
        voucher.setInvoiceFor("Invoice for Order #" + order.getId());
        voucher.setCashier(cashier.getName());
        voucher.setReceivedBy(requestDTO.getReceivedBy());
        voucher.setApprovedBy(approvedBy.getName());
        voucher.setStatus(PaymentVoucher.PaymentStatus.valueOf(requestDTO.getStatus()));

        Settings settings = settingRepository.findLatestSettings();
        double currentPrice = settings.getCurrentLunchPrice();

        List<VoucherRow> voucherRows = order.getRows().stream().map(orderRow -> {
            VoucherRow row = new VoucherRow();
            row.setDt(orderRow.getLunchDate());
            row.setQty(orderRow.getQuantity());
            row.setPrice(currentPrice);
            row.setAmount(row.getQty() * row.getPrice());
            row.setRemark("Derived from OrderRow");
            row.setPaymentVoucher(voucher);
            return row;
        }).collect(Collectors.toList());

        voucher.setRows(voucherRows);

        return paymentVoucherRepository.save(voucher);
    }

    private String generateVoucherNo(Optional<String> latestVoucherNo, String year, String month) {
        int nextVoucherNumber = 1;
        if (latestVoucherNo.isPresent()) {
            String lastVoucher = latestVoucherNo.get();
            String lastNumber = lastVoucher.substring(lastVoucher.length() - 3); // Extract last 3 digits
            nextVoucherNumber = Integer.parseInt(lastNumber) + 1;
        }

        return String.format("CS-%s%s%03d", year, month, nextVoucherNumber);
    }
}