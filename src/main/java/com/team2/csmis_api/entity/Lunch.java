package com.team2.csmis_api.entity;

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

    @ElementCollection
    @Column(name = "menu")
    private List<String> menu;

    @Column(name = "price")
    private String price;

    @Column(name = "company_rate")
    private String companyRate;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
