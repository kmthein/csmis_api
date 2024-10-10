package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "team")
@Entity
public class Team extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;
}
