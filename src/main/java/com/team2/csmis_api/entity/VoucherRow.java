package com.team2.csmis_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "voucher_rows")
public class VoucherRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "dt", nullable = false)
    private LocalDate dt; // Using LocalDate instead of Date

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "remark")
    private String remark;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "payment_voucher_id")
    private PaymentVoucher paymentVoucher;  // Reference to PaymentVoucher

    // Constructors, Getters, and Setters

    public VoucherRow() {}

    public VoucherRow(String dt, int qty, double price, double amount) {
        this.dt = LocalDate.parse(dt);
        this.qty = qty;
        this.price = price;
        this.amount = amount;
    }

}