package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Settings extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "last_register_day")
    private String lastRegisterDay;

    @Column(name = "last_register_time")
    private String lastRegisterTime;

    @Column(name = "company_rate")
    private Double compnayRate;

    @Column(name = "current_lunch_price")
    private Double currentLunchPrice;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User Admin;
}
