package com.team2.csmis_api.service;

import com.team2.csmis_api.dto.PaymentVoucherDTO;
import com.team2.csmis_api.dto.VoucherRowDTO;
import com.team2.csmis_api.entity.*;
import com.team2.csmis_api.exception.ResourceNotFoundException;
import com.team2.csmis_api.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class PaymentVoucherServiceImpl implements PaymentVoucherService {

    private final PaymentVoucherRepository paymentVoucherRepository;
    private final OrderRepository orderRepository;
    private final LunchRepository lunchRepo;
    private final UserRepository userRepository;
    private final VoucherRowRepository voucherRowRepository;

    @Override
    public List<PaymentVoucher> getAllPaymentVoucher() {
        return paymentVoucherRepository.findAll();
    }

    @Transactional
    public String createPaymentVoucherByDate(LocalDate selectedDate, PaymentVoucherDTO requestDTO) {
        // Get weekdays from the selected date
        List<LocalDate> weekdays = getWeekdaysFromSelectedDate(selectedDate);

        if (weekdays.isEmpty()) {
            throw new RuntimeException("Invalid selected date. Unable to calculate weekdays.");
        }

        // Find OrderRows matching these weekdays
        List<OrderRow> orderRows = orderRepository.findOrderRowsByDates(weekdays);

        // Handle case where no orders are found
        if (orderRows.isEmpty()) {
            throw new RuntimeException("No orders found for the selected date range: " + weekdays);
        }

        // Group OrderRows by orderId
        Map<Integer, List<OrderRow>> groupedOrderRows = orderRows.stream()
                .collect(Collectors.groupingBy(orderRow -> orderRow.getOrder().getId()));

        // Process each order to create PaymentVoucher
        for (Integer orderId : groupedOrderRows.keySet()) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found for orderId: " + orderId));

            // Pass selectedDate to the createVoucherFromOrder method
            createVoucherFromOrder(order, groupedOrderRows.get(orderId), requestDTO, selectedDate);
        }

        // Call the repository method with the current year and month as parameters
        String year = String.valueOf(LocalDate.now().getYear());
        String month = String.format("%02d", LocalDate.now().getMonthValue());

        Optional<String> latestVoucherNo = paymentVoucherRepository.findLatestVoucherNo(year, month);
        return latestVoucherNo.orElseThrow(() -> new RuntimeException("Failed to find the latest voucher number"));
    }

    private List<LocalDate> getWeekdaysFromSelectedDate(LocalDate selectedDate) {
        if (selectedDate == null) {
            throw new RuntimeException("Selected date cannot be null.");
        }

        // Find the Monday of the week of the selected date
        LocalDate startOfWeek = selectedDate.with(DayOfWeek.MONDAY);

        // Collect weekdays (Monday to Friday) for the same week
        return startOfWeek.datesUntil(startOfWeek.plusDays(5))  // Get 5 weekdays: Monday to Friday
                .collect(Collectors.toList());
    }


    private PaymentVoucher createVoucherFromOrder(Order order, List<OrderRow> orderRows, PaymentVoucherDTO requestDTO, LocalDate selectedDate) {
        String year = String.valueOf(LocalDate.now().getYear());
        String month = String.format("%02d", LocalDate.now().getMonthValue());

        // Fetch the latest voucher number
        Optional<String> latestVoucherNo = paymentVoucherRepository.findLatestVoucherNo(year, month);
        String newVoucherNo = generateVoucherNo(latestVoucherNo, year, month);

        // Fetch the cashier based on user_id selected for the date
        User cashier = userRepository.findUserById(requestDTO.getCashierUserId());
        if (cashier == null) {
            throw new RuntimeException("Cashier user not found");
        }

        User approvedBy = userRepository.findByName(requestDTO.getApprovedByName());
        if (approvedBy == null) {
            throw new RuntimeException("ApprovedBy user (ADMIN) not found");
        }

        // Create the PaymentVoucher object
        PaymentVoucher voucher = new PaymentVoucher();
        voucher.setVoucherNo(newVoucherNo);
        voucher.setPaymentDate(LocalDate.now());
        voucher.setRestaurantName(order.getRestaurant().getName());
        voucher.setTotalAmount(requestDTO.getTotalAmount());

        // Calculate the Monday and Friday of the selected week
        List<LocalDate> weekdays = getWeekdaysFromSelectedDate(selectedDate);
        LocalDate monday = weekdays.get(0);
        LocalDate friday = weekdays.get(4);

        // Format the dates as DDMMYYYY
        String invoiceFor = String.format("%s~%s", formatDate(monday), formatDate(friday));

        // Set the invoiceFor field with the formatted date range
        voucher.setInvoiceFor(invoiceFor);
        voucher.setCashier(cashier.getName());
        voucher.setReceivedBy(requestDTO.getReceivedBy());
        voucher.setApprovedBy(approvedBy.getName());
        voucher.setStatus(PaymentVoucher.PaymentStatus.valueOf(requestDTO.getStatus()));

        // Create VoucherRows for each OrderRow
        List<VoucherRow> voucherRows = orderRows.stream().map(orderRow -> {
            // Get the lunch date for the current order row
            LocalDate lunchDate = orderRow.getLunchDate();
            System.out.println(orderRow.getLunchDate());

            // Check if lunch exists for the selected date; if not, find another date in the same week
            Lunch lunch = getLunchForWeek(lunchDate, weekdays);

            // Calculate price for the row
            double lunchPrice = lunch.getPrice();

            // Set the remark (taken from the UI input by the cashier)
            String remark = requestDTO.getRemark();  // The remark is taken from the requestDTO which is input by the cashier on the UI

            VoucherRow row = new VoucherRow();
            row.setDt(orderRow.getLunchDate());
            row.setQty(orderRow.getQuantity());
            row.setPrice(lunchPrice);
            row.setAmount(row.getQty() * row.getPrice()); // Calculate the total for each row
            row.setRemark(remark);  // Set the remark input by the cashier
            row.setPaymentVoucher(voucher);
            return row;
        }).collect(Collectors.toList());

        voucher.setRows(voucherRows);

        // Save the PaymentVoucher and return
        return paymentVoucherRepository.save(voucher);
    }


    private Lunch getLunchForWeek(LocalDate selectedDate, List<LocalDate> weekdays) {
        // Try to fetch lunch for the selected date
        Optional<Lunch> lunchOpt = lunchRepo.findByDate(selectedDate);

        if (lunchOpt.isPresent()) {
            return lunchOpt.get();  // Return the lunch if found
        } else {
            // If no lunch is found for the selected date, find any lunch from the same week
            for (LocalDate weekday : weekdays) {
                Optional<Lunch> fallbackLunch = lunchRepo.findByDate(weekday);
                if (fallbackLunch.isPresent()) {
                    return fallbackLunch.get(); // Return the first available lunch for the week
                }
            }
            throw new RuntimeException("No lunch found for the selected week.");
        }
    }


    // Helper method to format the date as DDMMYYYY
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
    }


    private String generateVoucherNo(Optional<String> latestVoucherNo, String year, String month) {
        // Default the nextVoucherNumber to 1 if no previous voucher number is found
        int nextVoucherNumber = 1;

        // If the latest voucher number is present, extract the next number
        if (latestVoucherNo.isPresent()) {
            String lastVoucher = latestVoucherNo.get();
            String lastNumber = lastVoucher.substring(lastVoucher.length() - 3); // Extract last 3 digits
            try {
                nextVoucherNumber = Integer.parseInt(lastNumber) + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("Error parsing voucher number", e);
            }
        }

        // Return the formatted voucher number with leading zeros for the next number
        return String.format("CS-%s%s%03d", year, month, nextVoucherNumber);
    }

    //*****************************UPDATE****************************//
    //*****************************UPDATE****************************//
    //*****************************UPDATE****************************//
    //*****************************UPDATE****************************//
    //*****************************UPDATE****************************//

    @Transactional
    public void updatePaymentVoucher(Integer id, PaymentVoucherDTO requestDTO) {
        // Fetch the existing PaymentVoucher by ID
        PaymentVoucher existingVoucher = paymentVoucherRepository.findById(id).orElseThrow(
                () -> new RuntimeException("PaymentVoucher with id " + id + " not found")
        );

        // Update Cashier
        if (requestDTO.getCashierUserId() != null) {
            User cashier = userRepository.findById(requestDTO.getCashierUserId()).orElseThrow(
                    () -> new RuntimeException("Cashier user not found")
            );
            existingVoucher.setCashier(cashier.getName()); // Assuming 'username' is the field to set
        }

        // Update Approved By
        if (requestDTO.getApprovedByName() != null) {
            User approvedBy = userRepository.findByName(requestDTO.getApprovedByName());
            if(approvedBy == null) {
                throw new ResourceNotFoundException("Approve id not found");
            }
            existingVoucher.setApprovedBy(approvedBy.getName()); // Assuming 'username' is the field to set
        }

        // Update Received By
        if (requestDTO.getReceivedBy() != null) {
            existingVoucher.setReceivedBy(requestDTO.getReceivedBy());
        }

        // Update Payment Status
        if (requestDTO.getStatus() != null) {
            existingVoucher.setStatus(PaymentVoucher.PaymentStatus.valueOf(requestDTO.getStatus()));
        }

        // Update VoucherRow remarks
        if (requestDTO.getRows() != null) {
            for (VoucherRowDTO rowDTO : requestDTO.getRows()) {
                VoucherRow existingRow = voucherRowRepository.findById(rowDTO.getId()).orElseThrow(
                        () -> new RuntimeException("VoucherRow not found with ID: " + rowDTO.getId())
                );

                if (rowDTO.getRemark() != null) {
                    existingRow.setRemark(rowDTO.getRemark());
                }

                voucherRowRepository.save(existingRow); // Save the updated VoucherRow
            }
        }

        // Save the updated PaymentVoucher
        paymentVoucherRepository.save(existingVoucher);
    }
    //****************************************************************//
    //****************************************************************//
    //****************************************************************//
    //****************************************************************//

    // Delete Payment Voucher by ID (soft delete)
    @Transactional
    public void deletePaymentVoucher(Integer id) {
        PaymentVoucher paymentVoucher = paymentVoucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment Voucher with id " + id + " not found"));
        paymentVoucher.setIsDeleted(true);
        paymentVoucherRepository.save(paymentVoucher); // Save the soft-deleted entity
    }


    // Get Payment Voucher by ID
    public PaymentVoucher getPaymentVoucherById(Integer id) {
        return paymentVoucherRepository.findById(id)
                .filter(voucher -> !voucher.getIsDeleted()) // Exclude soft-deleted voucher
                .orElseThrow(() -> new RuntimeException("Payment Voucher with id " + id + " not found or has been deleted"));
    }

}
