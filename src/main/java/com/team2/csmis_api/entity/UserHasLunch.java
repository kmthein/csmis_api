package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "user_has_lunch")
@Entity
public class UserHasLunch extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_cost")
    private double user_cost;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user_id;

    @ManyToOne
    @JoinColumn(name="lunch_id")
    private Lunch lunch_id;
}
