package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Table(name = "payment_voucher")
@Entity
public class PaymentVoucher extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "voucherNo", columnDefinition = "TEXT")
    private String voucherNo;

    @Column(name = "restaurantName", columnDefinition = "TEXT")
    private String restaurantName;

    @Column(name = "paymentDate")
    private LocalDate paymentDate;

    @Column(name = "invoiceFor", columnDefinition = "TEXT")
    private String invoiceFor;


    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VoucherRow> rows;


    @Column(name = "cashier", columnDefinition = "TEXT")
    private String cashier;

    @Column(name = "receivedBy", columnDefinition = "TEXT")
    private String receivedBy;

    @Column(name = "approvedBy", columnDefinition = "TEXT")
    private String approvedBy;




}
