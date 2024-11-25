package com.team2.csmis_api.entity;

import com.team2.csmis_api.converter.StringArrayConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Table(name = "lunch")
@Entity
public class Lunch extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "menu", columnDefinition = "TEXT")
    private String menu;

    @Column(name = "price")
    private Double price;

    @Column(name = "company_rate")
    private Double companyRate;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
