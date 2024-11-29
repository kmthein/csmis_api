package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "payment_vouchers")
public class PaymentVoucher extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "voucherNo", nullable = false)
    private String voucherNo;

    @Column(name = "paymentDate", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "restaurantName", nullable = false)
    private String restaurantName;

    @Column(name = "invoiceFor", nullable = false)
    private String invoiceFor;

//    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL)
//    private List<VoucherRow> rows;

    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<VoucherRow> rows;


    @Column(name = "cashier", nullable = false)
    private String cashier;

    @Column(name = "receivedBy", nullable = false)
    private String receivedBy;

    @Column(name = "approvedBy", nullable = false)
    private String approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status; // Using Enum instead of String

    @Column(name = "total_amount")
    private Double totalAmount;

    public enum PaymentStatus {
        PAID,
        UNPAID
    }
}