package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
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
    private Double companyRate;

    @Column(name = "current_lunch_price")
    private Double currentLunchPrice;

    @Column(name = "lunchReminderTime")
    private LocalTime lunchReminderTime;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User Admin;
}
