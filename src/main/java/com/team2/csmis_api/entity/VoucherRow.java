package com.team2.csmis_api.entity;

import com.team2.csmis_api.entity.PaymentVoucher;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "voucher_rows")
public class VoucherRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lunch_date", nullable = false)
    private LocalDate lunchDate;

    @Column(name = "pax", nullable = false)
    private Integer pax;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_voucher_id", nullable = false)
    private PaymentVoucher paymentVoucher;


}
