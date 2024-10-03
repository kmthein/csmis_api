package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "user")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "staff_id")
    private String staff_id;

    @Column(name = "door_log_no")
    private String door_log_no;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "is_vegan", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVegan = false;

    @Column(name = "role")
    private String role;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @ManyToOne
    @JoinColumn(name="division_id")
    private Division division_id;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department_id;

    @ManyToOne
    @JoinColumn(name="team_id")
    private Team team_id;

    @ManyToMany
    @JoinTable(
            name = "user_avoid_meat",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meat_id")
    )
    private List<Meat> meats;

    @ManyToMany
    @JoinTable(
            name = "user_has_notification",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "notification_id")
    )
    private List<Notification> notifications;




}
