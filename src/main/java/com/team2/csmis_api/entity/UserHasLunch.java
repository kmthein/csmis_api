package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Table(name = "user_has_lunch")
@Entity
public class UserHasLunch extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "dt")
    private Date dt;

    @Column(name = "user_cost")
    private double userCost;

    @Column(name = "company_cost")
    private Double company_cost;

    @Column(name = "total_cost")
    private Double total_cost;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="lunch_id")
    private Lunch lunch;
}
