package com.team2.csmis_api.entity;

import com.team2.csmis_api.converter.StringArrayConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Table(name = "lunch")
@Entity
public class Lunch extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Convert(converter = StringArrayConverter.class)
    @Column(name = "menu")
    private String[] menu;

    @Column(name = "price")
    private double price;

    @Column(name = "company_rate")
    private double companyRate;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
