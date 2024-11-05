package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "voucher")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "voucher_code")
    private String voucherCode;

    @Column(name = "period")
    private String period;

    @Column(name = "cashier")
    private String cashier;

    @Column(name = "received_by")
    private String receivedBy;

    @Column(name = "approver")
    private String approver;

    @Column(name = "number_of_pax")
    private int numberOfPax;

    @Column(name = "price")
    private int price;

    @Column(name = "amount")
    private int amount;

    @Column(name = "payment_date")
    private String paymentDate;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_method")
    private String paymentMethod;
}
